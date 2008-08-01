package org.yeastrc.ms.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.impl.MsExperimentDbImpl;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.Sha1SumCalculator;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);


    private List<Integer> searchIdList = new ArrayList<Integer>();
    private int runExperimentId;
    private int searchGroupId;
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private static final Pattern fileNamePattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return database id for experiment if it was uploaded successfully, 0 otherwise
     */
    public int uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) {

        searchIdList.clear();
        uploadExceptionList.clear();
        runExperimentId = 0;
        searchGroupId = 0;
        
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+fileDirectory+
                "\n\tTime: "+(new Date().toString()));
        long start = System.currentTimeMillis();
        
        // get the file names
        Set<String> filenames = getFileNamePrefixes(fileDirectory);
        
        // ----- BEFORE BEGINNING UPLOAD MAKE THE FOLLOWING CHECKS -----
        // (1). If we didn't find anything print warning and return.
        if (filenames.size() == 0) {
            UploadException ex = new UploadException(ERROR_CODE.EMPTY_DIRECTORY);
            ex.setDirectory(fileDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return 0;
        }
        
        // (2). make sure .ms2 files are present
        String missingFile = missingMs2File(fileDirectory, filenames);
        if (missingFile != null) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_MS2);
            ex.setErrorMessage("Missing file: "+missingFile);
            ex.setDirectory(fileDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return 0;
        }
        
        
        // ----- NOW WE CAN BEGIN UPLOAD -----
        int experimentId = 0;
        
        experimentId =  uploadExperiment(remoteServer, remoteDirectory, fileDirectory);
        searchGroupId = getMySearchGroupId();
        try {
            runExperimentId = uploadRunAndSearchFilesToDb(experimentId, fileDirectory, filenames, searchGroupId);
        }
        catch (UploadException e) {
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            deleteSearches(searchIdList);
            deleteExperiment(experimentId);
        }

        
        // If the runs in this upload were already uploaded as part of another
        // experiment, delete the entry we created earlier in the msExperiment table
        if (experimentId != runExperimentId) {
            deleteExperiment(experimentId);
            experimentId = 0;
        }
        
        long end = System.currentTimeMillis();
        if (experimentId != 0)
            log.info("EXPERIMENT (id: "+experimentId+") UPLOADED IN: "
                    +((end - start)/(1000L))+"seconds\n\tTime: "+(new Date()).toString()+"\n\n");
        else
            log.info("EXPERIMENT UPLOADED IN: "
                    +((end - start)/(1000L))+"seconds\n\tONLY SQT FILES WERE UPLOADED for existing experimentID: "+runExperimentId+"\n\tTime: "+(new Date()).toString()+"\n\n");
        return runExperimentId;
    }

    private int uploadExperiment(String remoteServer, String remoteDirectory, String fileDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperimentDbImpl experiment = new MsExperimentDbImpl();
        experiment.setDate(new java.sql.Date(new Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }

    private int getMySearchGroupId() {
        return DAOFactory.instance().getMsSearchDAO().getMaxSearchGroupId() + 1; // one more than the last search group id.
    }
    
    public int getSearchGroupId() {
        return searchGroupId;
    }
    
    public List<Integer> getSearchIdList() {
        List<Integer> copyList = new ArrayList<Integer>(searchIdList.size());
        for (Integer id: searchIdList)
            copyList.add(id);
        return copyList;
    }
    
    /**
     * If the runs were already in the database, the runs are not uploaded again, and the 
     * existing experimentId for the runs is returned. 
     * @param experimentId
     * @param fileDirectory
     * @param filenames
     * @return
     * @throws UploadException 
     */
    private int uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory, Set<String> filenames, int searchGroupId) throws UploadException {
        
        boolean firstIter = true;
        for (String filename: filenames) {

            // Upload the run first.
            int runId = uploadMS2Run(fileDirectory+File.separator+filename+".ms2",experimentId);
            
            // Get the experimentId for this run.  If this run is already in the database
            // it will have a experiment id different from the one given to us as arguments
            // to this method.
            int tempId = MS2DataUploadService.getExperimentIdForRun(runId);
            if (firstIter) {
                experimentId = tempId;
                firstIter = false;
            }
            // Make sure all runs in this experiment have the same experimentID!
            else if (tempId != experimentId) {
                UploadException ex = new UploadException(ERROR_CODE.MULTIPLE_EXPIDS);
                ex.setDirectory(fileDirectory);
                ex.setErrorMessage("Runs in an experiment upload cannot have different experimentIds! Found ids: "+experimentId+", "+tempId);
                throw ex;
            }

            // if the sqt file does not exist go on to the next run
            String sqtFile = fileDirectory+File.separator+filename+".sqt";
            if (!(new File(sqtFile).exists()))
                continue;
            
            // now upload the search result 
            uploadSQTSearch(fileDirectory+File.separator+filename+".sqt", runId, searchGroupId);
        }
        
        return experimentId;
    }

    private int uploadMS2Run(String filePath, int experimentId) throws UploadException {
        Ms2FileReader ms2Provider = new Ms2FileReader();
        MS2DataUploadService uploadService = new MS2DataUploadService();
        String sha1Sum;
        try {
            sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        }
        catch (Exception e) {
            UploadException ex = new UploadException(ERROR_CODE.SHA1SUM_CALC_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage());
            log.error(ex.getMessage(), ex);
            uploadExceptionList.add(ex);
            throw ex;
        }
        
        try {
            ms2Provider.open(filePath, sha1Sum);
            return uploadService.uploadMS2Run(ms2Provider,experimentId, sha1Sum);
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_MS2, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage());
            log.error(ex.getMessage(), ex);
            uploadExceptionList.add(ex);
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.UNKNOWN_MS2_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage());
            log.error(ex.getMessage(), ex);
            uploadExceptionList.add(ex);
            throw ex;
        }
        finally {
            ms2Provider.close();
        }
    }
    
    private int uploadSQTSearch(String filePath, int runId, int searchGroupId) {
        
        // is this a supported SQT file
        try {
            if (!SQTFileReader.isSequestSQT(filePath)) {
                createAndLogUploadException(ERROR_CODE.UNSUPPORTED_SQT, null, filePath, null, null);
                return 0;
            }
        }
        catch (IOException e1) {
            createAndLogUploadException(ERROR_CODE.READ_ERROR_SQT, e1, filePath, null, e1.getMessage());
            return 0;
        }
        
        // upload the file
        SQTFileReader sqtProvider = new SQTFileReader();
        SQTDataUploadService uploadService = new SQTDataUploadService();
        int searchId = 0;
        try { 
            sqtProvider.open(filePath);
            searchId = uploadService.uploadSQTSearch(sqtProvider, runId, searchGroupId);
        }
        catch (DataProviderException e) {
            createAndLogUploadException(ERROR_CODE.READ_ERROR_SQT, e, filePath, null, e.getMessage());
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            if (searchId != 0) {
                SQTDataUploadService.deleteSearch(searchId);
                searchId = 0;
            }
        }
        catch (UploadException e) {
            e.setFile(filePath);
            log.error(e.getMessage(), e);
            uploadExceptionList.add(e);
            searchId = uploadService.getUploadedSearchId();
            if (searchId != 0) {
                SQTDataUploadService.deleteSearch(searchId);
                searchId = 0;
            }
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            createAndLogUploadException(ERROR_CODE.UNKNOWN_SQT_ERROR, e, filePath, null, e.getMessage());
            searchId = uploadService.getUploadedSearchId();
            if (searchId != 0) {
                SQTDataUploadService.deleteSearch(searchId);
                searchId = 0;
            }
        }
        
        finally {
            if (searchId != 0)
                searchIdList.add(searchId);
            sqtProvider.close(); // close open file
        }
        return searchId;
    }
    
    private UploadException createAndLogUploadException(ERROR_CODE errCode, Exception sourceException, String file, String directory, String message) {
        UploadException ex = null;
        if (sourceException == null)
            ex = new UploadException(errCode);
        else
            ex = new UploadException(errCode, sourceException);
        ex.setFile(file);
        ex.setDirectory(directory);
        ex.setErrorMessage(message);
        uploadExceptionList.add(ex);
        log.error(ex.getMessage(), ex);
        return ex;
    }
    
    /**
     * Check for .ms2 files. 
     * @param fileDirectory
     * @param filenames
     * @return
     */
    private String missingMs2File(String fileDirectory, Set<String> filenames) {
        for (String filePrefix: filenames) {
            if (!(new File(fileDirectory+File.separator+filePrefix+".ms2").exists())) {
                log.error("Required file: "+filePrefix+".ms2 not found");
                return filePrefix+".ms2";
            }
        }
        return null;
    }

    
    private Set<String> getFileNamePrefixes(String fileDirectory) {
        File directory = new File (fileDirectory);
        if (!directory.exists()) {
            throw new IllegalArgumentException("Directory does not exist: "+fileDirectory);
        }
        return getFileNamesInDirectory(directory);
    }

    private Set<String> getFileNamesInDirectory(File directory) {

        Set<String> filenames = new HashSet<String>();
        File[] files = directory.listFiles();
        String name = null;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (fileName.endsWith(".ms2") || fileName.endsWith(".sqt")) {
                name = files[i].getName();
                name = name.substring(0, name.indexOf('.'));
                filenames.add(name);
            }
        }
        return filenames;
    }
    
    private void deleteExperiment(int experimentId) {
        MS2DataUploadService.deleteExperiment(experimentId);
        log.error("DELETED RUNS, SEARCHES and EXPERIMENT for experimentID: "+experimentId);
    }
    
    private void deleteSearches(List<Integer> searchIdList) {
        for (Integer searchId: searchIdList) {
            SQTDataUploadService.deleteSearch(searchId);
            log.error("DELETED searchID: "+searchId);
        }
        
    }
    
    public static int getScanIdFor(int experimentId, String runFileScanString) {
     // parse the filename to get the scan number and filename
        // e.g. NE063005ph8s02.17247.17247.2
        Matcher match = fileNamePattern.matcher(runFileScanString);
        if (!match.matches()) {
            log.error("!!!INVALID FILENAME FROM DTASELECT RESULT: "+runFileScanString);
            return 0;
        }
        String filename = match.group(1)+".ms2";
        int scanNum = 0;
        try {
            scanNum = Integer.parseInt(match.group(2));
        }
        catch(NumberFormatException e) {
            log.error("!!!ERROR PARSING SCAN NUMBER FROM DTASELECT RESULT: "+match.group(2)+"; "+runFileScanString);
            return 0;
        }
        return getScanIdFor(experimentId, filename, scanNum);
    }
        
    public static int getScanIdFor(int experimentId, String runFileName, int scanNumber) {
        
        MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();
        int runId = runDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
        if (runId == 0) {
            log.error("!!!NO RUN FOUND FOR EXPERIMENT: "+experimentId+"; fileName: "+runFileName);
            return 0;
        }
        MsScanDAO<MsScan, MsScanDb>scanDao = DAOFactory.instance().getMsScanDAO();
        return scanDao.loadScanIdForScanNumRun(scanNumber, runId);
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        MsExperimentUploader uploader = new MsExperimentUploader();
//        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/1542/");
        uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", "/a/scratch/ms_data/1217528828156");
//      uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/MacCossData/sequest");
//        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/new_lib/resources/PARC/TEST");
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
