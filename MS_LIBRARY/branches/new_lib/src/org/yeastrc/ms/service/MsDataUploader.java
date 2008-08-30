package org.yeastrc.ms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.ms2file.MS2DataUploadService;
import org.yeastrc.ms.service.sqtfile.SequestSQTDataUploadService;
import org.yeastrc.ms.util.Sha1SumCalculator;

public class MsDataUploader {

    private static final Logger log = Logger.getLogger(MsDataUploader.class);
    
    private int searchId;
    private int numRunsToUpload = 0;
    private int numRunsUploaded = 0;
    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private static final Pattern fileNamePattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
    
    private void resetUploader() {
        uploadExceptionList.clear();
        searchId = 0;
        numRunsToUpload = 0;
        numRunsUploaded = 0;
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
     * @param remoteServer
     * @param remoteDirectory
     * @param fileDirectory
     * @return database id for experiment if it was uploaded successfully, 0 otherwise
     * @throws UploadException 
     */
    public int uploadExperimentToDb(String remoteServer, String remoteDirectory, String fileDirectory, Date searchDate) throws UploadException {

        resetUploader();
        
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+fileDirectory+
                "\n\tTime: "+(new Date().toString()));
        long start = System.currentTimeMillis();
        
        // ----- BEFORE BEGINNING UPLOAD MAKE THE FOLLOWING CHECKS -----
        // (1). Make sure upload directory exists
        if (!(new File(fileDirectory).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.DIRECTORY_NOT_FOUND);
            ex.setDirectory(fileDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // get the file names
        Set<String> filenames = getFileNamePrefixes(fileDirectory);
        
        // (2). If we didn't find any files print warning and return.
        if (filenames.size() == 0) {
            UploadException ex = new UploadException(ERROR_CODE.EMPTY_DIRECTORY);
            ex.setDirectory(fileDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // (3). make sure .ms2 files are present
        String missingFile = missingMs2File(fileDirectory, filenames);
        if (missingFile != null) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_MS2);
            ex.setErrorMessage("Missing file: "+missingFile);
            ex.setDirectory(fileDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        try {
            uploadRunAndSearchFilesToDb(fileDirectory, filenames, remoteServer, remoteDirectory, searchDate);
        }
        catch (UploadException e) { // this should only result from ms2 file upload
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            log.error("ABORTING EXPERIMENT UPLOAD!!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            numRunsUploaded = 0;
            e.appendErrorMessage("!!!MS2 and SQT files will not be uploaded!!!");
            throw e;
        }
        
        long end = System.currentTimeMillis();
        log.info("END EXPERIMENT (id: "+searchId+") UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+
                "\n\t#Runs in Directory: "+numRunsToUpload+"; #Uploaded: "+numRunsUploaded+
                "\n\t#Searches in Directory: "+numSearchesToUpload+"; #Uploaded: "+numSearchesUploaded+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+fileDirectory+
                "\n\n");
        
        return searchId;
    }

   
    private SearchFileFormat getSqtType(String fileDirectory, Set<String> filenames) throws UploadException {
        
        SearchFileFormat sqtType = null;
        
        // make sure all files are of the same type
        for (String file: filenames) {
            String sqtFile = fileDirectory+File.separator+file+".sqt";
            // first make sure the file exists
            if (!(new File(sqtFile).exists()))
                continue;
            SearchFileFormat myType = SQTFileReader.getSearchFileType(sqtFile);
            
            // For now we support only sequest, ee-normalized sequest and ProLuCID sqt files. 
            if (SearchFileFormat.SQT_SEQ != myType&& 
                    SearchFileFormat.SQT_NSEQ != myType&&
                    SearchFileFormat.SQT_PLUCID != myType) {
                UploadException ex = new UploadException(ERROR_CODE.UNSUPPORTED_SQT);
                ex.setFile(sqtFile);
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                throw ex;
            }

            if (sqtType == null) sqtType = myType;
            if (myType != sqtType) {
                UploadException ex = new UploadException(ERROR_CODE.MULTIPLE_SQT_TYPES);
                ex.setErrorMessage("Found SQT files of types: "+sqtType.getTypeName()+", "+myType.getTypeName());
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                throw ex;
            }
        }
        return sqtType;
    }
    
    /**
     * If the runs were already in the database, the runs are not uploaded again, 
     * @param fileDirectory
     * @param filenames
     * @param serverAddress
     * @param serverDirectory
     * @param sqtType
     * @return
     * @throws UploadException 
     * @throws UploadException 
     */
    private void uploadRunAndSearchFilesToDb(String fileDirectory, Set<String> filenames, 
            String serverAddress, String serverDirectory, Date searchDate) throws UploadException  {
        
        // upload the runs first. This could throw an upload exception
        Map<String, Integer> runIdMap = uploadRuns(fileDirectory, filenames, serverAddress, serverDirectory);
        
        // now upload the searches
        uploadSearches(fileDirectory, filenames, serverAddress,
                        serverDirectory, searchDate, runIdMap);
    }

    // returns a mapping of filenames to runIDs
    private Map<String, Integer> uploadRuns(String fileDirectory, Set<String> filenames, String serverAddress, String serverDirectory) throws UploadException {
        Map<String, Integer> runIdMap = new HashMap<String, Integer>(filenames.size());
        for (String filename: filenames) {
            int runId = uploadMS2Run(fileDirectory+File.separator+filename+".ms2", serverAddress, serverDirectory);
            runIdMap.put(filename, runId);
        }
        return runIdMap;
    }
    

    // Any exceptions that happens during sha1sum calculation, parsing or upload
    // will be propagated up to the calling function. 
    private int uploadMS2Run(String filePath, String serverAddress, String serverDirectory) throws UploadException {
        numRunsToUpload++;
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
            int runId = uploadService.uploadMS2Run(ms2Provider, sha1Sum, serverAddress, serverDirectory);
            numRunsUploaded++;
            return runId;
        }
        catch (DataProviderException e) {
            UploadException ex = logAndAddUploadException(ERROR_CODE.READ_ERROR_MS2, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = logAndAddUploadException(ERROR_CODE.RUNTIME_MS2_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch(UploadException e) {
            e.setFile(filePath);
            throw e;
        }
        finally {
            ms2Provider.close();
        }
    }
    
    private void uploadSearches(String fileDirectory, Set<String> filenames,
            String serverAddress, String serverDirectory, Date searchDate,
            Map<String, Integer> runIdMap) {
        
        // make sure there are no unsupported .sqt files. Get the sqt file type. This method may throw an UploadException. 
        SearchFileFormat sqtType = null;
        try {
            sqtType = getSqtType(fileDirectory, filenames);
        }
        catch (UploadException e) {
            return; // don't go forward if there was a problem getting the sqt file type.
        }
        
        // parse the parameters file and create a new entry in the msSearch table
        if (sqtType == SearchFileFormat.SQT_SEQ) {
            uploadSequestSearches(fileDirectory, filenames, serverAddress, serverDirectory, runIdMap, searchDate);
            
        }
        else if (sqtType == SearchFileFormat.SQT_PLUCID) {
            uploadProlucidSearches(fileDirectory, filenames, serverAddress, serverDirectory, runIdMap, searchDate);
        }
        else {
            UploadException ex = new UploadException(ERROR_CODE.UNKNOWN_PARAMS);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
    }
    
    // upload sequest sqt files
    private void uploadSequestSearches(String fileDirectory,
            Set<String> filenames, final String serverAddress,
            final String serverDirectory, Map<String, Integer> runIdMap,
            final Date searchDate) {
        
        SequestSQTDataUploadService service = new SequestSQTDataUploadService();
        service.uploadSQTSearch(fileDirectory, filenames, runIdMap, serverAddress, serverDirectory, new java.sql.Date(searchDate.getTime()));
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.searchId = service.getUploadedSearchId();
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
    }

    // upload prolucid sqt files
    private void uploadProlucidSearches(String fileDirectory,
            Set<String> filenames, String serverAddress,
            String serverDirectory, Map<String, Integer> runIdMap,
            Date searchDate) {
//        this.uploadExceptionList.addAll(service.getUploadExceptionList());
//        this.searchId = service.getUploadedSearchId();
//        this.numSearchesToUpload = service.getNumSearchesToUpload();
//        this.numSearchesUploaded = service.getNumSearchesUploaded();
        // TODO

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
    
    
    public static int getScanIdFor(String runFileScanString, int searchId) {
        // parse the filename to get the filename, scan number and charge
        // e.g. NE063005ph8s02.17247.17247.2
        Matcher match = fileNamePattern.matcher(runFileScanString);
        if (!match.matches()) {
            log.error("!!!INVALID FILENAME FROM DTASELECT RESULT: "+runFileScanString);
            return 0;
        }
        String runFileName = match.group(1)+".ms2";
        int scanNum = Integer.parseInt(match.group(2));
        
        MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();
        int runId = runDao.loadRunIdForSearchAndFileName(searchId, runFileName);
        if (runId == 0) {
            log.error("!!!NO RUN FOUND FOR SearchId: "+searchId+"; fileName: "+runFileName);
            return 0;
        }
        
        MsScanDAO<MsScan, MsScanDb>scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNum, runId);
        if (scanId == 0) {
            log.error("!!!NO SCAN FOUND FOR SCAN NUMBER: "+scanNum+"; runId: "+runId+"; fileName: "+runFileName);
            return 0;
        }
        
        return scanId;
    }
    
    public static void main(String[] args) throws UploadException {
        long start = System.currentTimeMillis();
        MsDataUploader uploader = new MsDataUploader();
//        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/1542/");
        uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", "/a/scratch/ms_data/1217528828156", new Date());
//      uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/MacCossData/sequest");
//        uploader.uploadExperimentToDb("serverPath", "serverDirectory", "/Users/vagisha/WORK/MS_LIBRARY/new_lib/resources/PARC/TEST");
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
