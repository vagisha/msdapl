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
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.impl.MsExperimentDbImpl;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.Sha1SumCalculator;

public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);


    private int runExperimentId;
    private int numRunsToUpload = 0;
    private int numRunsUploaded = 0;
    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private static final Pattern fileNamePattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
    
    /**
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return database id for experiment if it was uploaded successfully, 0 otherwise
     * @throws UploadException 
     */
    public int uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory) throws UploadException {

        resetUploader();
        
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+fileDirectory+
                "\n\tTime: "+(new Date().toString()));
        long start = System.currentTimeMillis();
        
        // get the file names
        Set<String> filenames = getFileNamePrefixes(fileDirectory);
        
        // ----- BEFORE BEGINNING UPLOAD MAKE THE FOLLOWING CHECKS -----
        // (1). If we didn't find any files print warning and return.
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
        
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        // make a new entry in the msExperment table first
        runExperimentId =  uploadExperiment(remoteServer, remoteDirectory);
        
        try {
            uploadRunAndSearchFilesToDb(runExperimentId, fileDirectory, filenames);
        }
        catch (UploadException e) {
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            log.error("ABORTING EXPERIMENT UPLOAD!!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            deleteExperiment(runExperimentId); // delete the experiment
            numRunsUploaded = 0;
            numSearchesUploaded = 0;
            throw e;
        }
        
        long end = System.currentTimeMillis();
        log.info("END EXPERIMENT (id: "+runExperimentId+") UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+
                "\n\t#Runs in Directory: "+numRunsToUpload+"; #Uploaded: "+numRunsUploaded+
                "\n\t#Searches in Directory: "+numSearchesToUpload+"; #Uploaded: "+numSearchesUploaded+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+fileDirectory+
                "\n\n");
        
        return runExperimentId;
    }

    private void resetUploader() {
        uploadExceptionList.clear();
        runExperimentId = 0;
        numRunsToUpload = 0;
        numRunsUploaded = 0;
        numSearchesToUpload = 0;
        numSearchesUploaded = 0;
    }

    private int uploadExperiment(String remoteServer, String remoteDirectory) {
        MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperimentDbImpl experiment = new MsExperimentDbImpl();
        experiment.setDate(new java.sql.Date(new Date().getTime()));
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        return expDao.save(experiment);
    }
    
    public List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }
    
    public String getUploadWarnings() {
        StringBuilder buf = new StringBuilder();
        for (UploadException e: uploadExceptionList) {
            buf.append(e.getMessage()+"\n");
        }
        return buf.toString();
    }
    
    /**
     * If the runs were already in the database, the runs are not uploaded again, but a new entry is 
     * created in msExperimentRun linking the run with the new experiment.
     * @param experimentId
     * @param fileDirectory
     * @param filenames
     * @return
     * @throws UploadException 
     */
    private void uploadRunAndSearchFilesToDb(int experimentId, String fileDirectory, Set<String> filenames) throws UploadException {
        
        for (String filename: filenames) {

            // Upload the run first.
            int runId = uploadMS2Run(fileDirectory+File.separator+filename+".ms2",experimentId);
            
            // if the sqt file does not exist go on to the next run
            String sqtFile = fileDirectory+File.separator+filename+".sqt";
            if (!(new File(sqtFile).exists()))
                continue;
            
            // now upload the search result 
            uploadSQTSearch(fileDirectory+File.separator+filename+".sqt", runId, experimentId);
        }
    }

    // Any exceptions that happens during sha1sum calculation, parsing or upload
    // will be punted up to the calling function. 
    private int uploadMS2Run(String filePath, int experimentId) throws UploadException {
        Ms2FileReader ms2Provider = new Ms2FileReader();
        MS2DataUploadService uploadService = new MS2DataUploadService();
        String sha1Sum;
        try {
            sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        }
        catch (Exception e) {
            UploadException ex = logAndAddUploadException(ERROR_CODE.SHA1SUM_CALC_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        
        try {
            ms2Provider.open(filePath, sha1Sum);
            return uploadService.uploadMS2Run(ms2Provider,experimentId, sha1Sum);
        }
        catch (DataProviderException e) {
            UploadException ex = logAndAddUploadException(ERROR_CODE.READ_ERROR_MS2, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = logAndAddUploadException(ERROR_CODE.RUNTIME_MS2_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        finally {
            ms2Provider.close();
        }
    }
    
    // Consume any exceptions during parsing and upload. If exceptions occur, this search will be deleted
    // but the experiment upload will continue.
    private int uploadSQTSearch(String filePath, int runId, int experimentId) {
        
        // is this a supported SQT file
        try {
            if (!SQTFileReader.isSequestSQT(filePath)) {
                logAndAddUploadException(ERROR_CODE.UNSUPPORTED_SQT, null, filePath, null, null);
                return 0;
            }
        }
        catch (IOException e1) {
            logAndAddUploadException(ERROR_CODE.READ_ERROR_SQT, e1, filePath, null, e1.getMessage());
            return 0;
        }
        
        // upload the file
        SQTFileReader sqtProvider = new SQTFileReader();
        SQTDataUploadService uploadService = new SQTDataUploadService();
        int searchId = 0;
        try { 
            sqtProvider.open(filePath);
            searchId = uploadService.uploadSQTSearch(sqtProvider, runId, experimentId);
        }
        catch (DataProviderException e) {
            logAndAddUploadException(ERROR_CODE.READ_ERROR_SQT, e, filePath, null, e.getMessage());
            deleteLastUploadedSearch(uploadService);
            return 0;
        }
        catch (UploadException e) {
            e.setFile(filePath);
            log.error(e.getMessage(), e);
            uploadExceptionList.add(e);
            deleteLastUploadedSearch(uploadService);
            return 0;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            logAndAddUploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e, filePath, null, e.getMessage());
            deleteLastUploadedSearch(uploadService);
            return 0;
        }
        
        finally {
            sqtProvider.close(); // close open file
        }
        return searchId;
    }

    private void deleteLastUploadedSearch(SQTDataUploadService uploadService) {
        int searchId = uploadService.getUploadedSearchId();
        if (searchId != 0) {
            SQTDataUploadService.deleteSearch(searchId);
        }
    }
    
    private UploadException logAndAddUploadException(ERROR_CODE errCode, Exception sourceException, String file, String directory, String message) {
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
    
    public static int[] getScanAndSearchIdFor(int experimentId, int searchGroupId, String runFileScanString) {
        // parse the filename to get the filename, scan number and charge
        // e.g. NE063005ph8s02.17247.17247.2
        Matcher match = fileNamePattern.matcher(runFileScanString);
        if (!match.matches()) {
            log.error("!!!INVALID FILENAME FROM DTASELECT RESULT: "+runFileScanString);
            return new int[0];
        }
        String runFileName = match.group(1)+".ms2";
        int scanNum = Integer.parseInt(match.group(2));
        
        MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();
        int runId = runDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
        if (runId == 0) {
            log.error("!!!NO RUN FOUND FOR EXPERIMENT: "+experimentId+"; fileName: "+runFileName);
            return new int[0];
        }
        
        MsScanDAO<MsScan, MsScanDb>scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNum, runId);
        if (scanId == 0) {
            log.error("!!!NO SCAN FOUND FOR SCAN NUMBER: "+scanNum+"; runId: "+runId+"; fileName: "+runFileName);
            return new int[0];
        }
        
        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        int searchId = searchDao.loadSearchIdForRunAndExperiment(runId, searchGroupId);
        if (searchId == 0) {
            log.error("!!!NO SEARCH FOUND FOR RUNID: "+runId+"; searchGroupId: "+searchGroupId+"; fileName: "+runFileName);
            return new int[0];
        }
        
        return new int[]{scanId, searchId};
    }
    
    public static void main(String[] args) throws UploadException {
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
