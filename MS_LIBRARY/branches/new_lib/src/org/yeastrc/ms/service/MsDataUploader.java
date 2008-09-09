package org.yeastrc.ms.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.ms2file.MS2DataUploadService;
import org.yeastrc.ms.service.sqtfile.ProlucidSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.SequestSQTDataUploadService;

public class MsDataUploader {

    private static final Logger log = Logger.getLogger(MsDataUploader.class);
    
    private int numRunsToUpload = 0;
    private int numRunsUploaded = 0;
    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private static final Pattern fileNamePattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
    
    private void resetUploader() {
        uploadExceptionList.clear();
        numRunsToUpload = 0;
        numRunsUploaded = 0;
        numSearchesToUpload = 0;
        numSearchesUploaded = 0;
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
        int searchId = 0;
        try {
            searchId = uploadRunAndSearchFilesToDb(fileDirectory, filenames, remoteServer, remoteDirectory, searchDate);
        }
        catch (UploadException e) { // this should only result from ms2 file upload
                                    // or if unsupported sqt files were found.
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            log.error("ABORTING EXPERIMENT UPLOAD!!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            numRunsUploaded = 0;
            throw e;
        }
        
        long end = System.currentTimeMillis();
        log.info("END EXPERIMENT UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+
                "\n\t#Runs in Directory: "+numRunsToUpload+"; #Uploaded: "+numRunsUploaded+
                "\n\tSEARCH ID: "+searchId+
                "\n\t#Searches in Directory: "+numSearchesToUpload+"; #Uploaded: "+numSearchesUploaded+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tUpload Directory: "+fileDirectory+
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
            if (SearchFileFormat.SQT_SEQ != myType && 
                    SearchFileFormat.SQT_NSEQ != myType &&
                    SearchFileFormat.SQT_PLUCID != myType) {
                UploadException ex = new UploadException(ERROR_CODE.UNSUPPORTED_SQT);
                ex.setFile(sqtFile);
                throw ex;
            }

            if (sqtType == null) sqtType = myType;
            if (myType != sqtType) {
                UploadException ex = new UploadException(ERROR_CODE.MULTIPLE_SQT_TYPES);
                ex.setErrorMessage("Found SQT files of types: "+sqtType.getFormatType()+", "+myType.getFormatType());
                throw ex;
            }
        }
        if (sqtType == null) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SQT_TYPE);
            throw ex;
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
     * @return searchId
     * @throws UploadException
     */
    private int uploadRunAndSearchFilesToDb(String fileDirectory, Set<String> filenames, 
            String serverAddress, String serverDirectory, Date searchDate) throws UploadException   {
        
        // upload the runs first. This could throw an upload exception
        Map<String, Integer> runIdMap;
        try {
            runIdMap = uploadRuns(fileDirectory, filenames, serverAddress, serverDirectory);
        }
        catch (UploadException e) {
            e.appendErrorMessage("!!!\n\tERROR UPLOADING MS2 DATA. SEARCH WILL NOT BE UPLOADED\n!!!");
            log.error(e.getMessage(), e);
            numRunsUploaded = 0;
            throw e;
        }
        
        // determine the type of sqt files we have
        // make sure there are no unsupported .sqt files. This method may throw an UploadException. 
        // We will propagate this exception
        SearchFileFormat sqtType = null;
        try {sqtType = getSqtType(fileDirectory, filenames);}
        catch(UploadException ex) {
            ex.appendErrorMessage("\n\tSEARCH WILL NOT BE UPLOADED.");
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // now upload the searches. No exception will be thrown if the upload fails
        try {
            return uploadSearches(fileDirectory, filenames, serverAddress,
                        serverDirectory, searchDate, runIdMap, sqtType);
        }
        // We should have caught all exceptions in the SQT upload classes but just in case anything slipped through...
        catch(RuntimeException e) {
            log.error("!!!ERROR UPLOADING SEARCH (RuntimeException)!!!", e);
            return 0;
        }
    }

    /**
     * Returns a mapping of filenames to runIDs
     * @param fileDirectory
     * @param filenames
     * @param serverAddress
     * @param serverDirectory
     * @return
     * @throws UploadException
     */
    private Map<String, Integer> uploadRuns(String fileDirectory, Set<String> filenames, String serverAddress, String serverDirectory) throws UploadException {
        MS2DataUploadService uploadService = new MS2DataUploadService();
        Map<String, Integer> runMapIds = uploadService.uploadRuns(fileDirectory, filenames, serverAddress, serverDirectory);
        this.numRunsToUpload = uploadService.getNumRunsToUpload();
        this.numRunsUploaded = uploadService.getNumRunsUploaded();
        this.uploadExceptionList.addAll(uploadService.getUploadExceptionList());
        return runMapIds;
    }

    /**
     * @param fileDirectory
     * @param filenames
     * @param serverAddress
     * @param serverDirectory
     * @param searchDate
     * @param runIdMap
     * @return searchId
     * @param sqtType
     */
    private int uploadSearches(String fileDirectory, Set<String> filenames,
            String serverAddress, String serverDirectory, Date searchDate,
            Map<String, Integer> runIdMap, SearchFileFormat sqtType) {
        
        // upload the search
        if (sqtType == SearchFileFormat.SQT_SEQ || sqtType == SearchFileFormat.SQT_NSEQ) {
            return uploadSequestSearch(fileDirectory, filenames, serverAddress, serverDirectory, runIdMap, searchDate);
        }
        else if (sqtType == SearchFileFormat.SQT_PLUCID) {
            return uploadProlucidSearch(fileDirectory, filenames, serverAddress, serverDirectory, runIdMap, searchDate);
        }
        else {
            log.error("Unknow SQT type");
            return 0;
        }
    }
    
    // upload sequest sqt files
    private int uploadSequestSearch(String fileDirectory,
            Set<String> filenames, final String serverAddress,
            final String serverDirectory, Map<String, Integer> runIdMap,
            final Date searchDate) {
        
        SequestSQTDataUploadService service = new SequestSQTDataUploadService();
        int searchId = service.uploadSearch(fileDirectory, filenames, runIdMap, serverAddress, serverDirectory, new java.sql.Date(searchDate.getTime()));
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
        return searchId;
    }

    // upload prolucid sqt files
    private int uploadProlucidSearch(String fileDirectory,
            Set<String> filenames, String serverAddress,
            String serverDirectory, Map<String, Integer> runIdMap,
            Date searchDate) {
        ProlucidSQTDataUploadService service = new ProlucidSQTDataUploadService();
        int searchId = service.uploadSearch(fileDirectory, filenames, runIdMap, serverAddress, serverDirectory, new java.sql.Date(searchDate.getTime()));
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
        return searchId;
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
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        int runId = runDao.loadRunIdForSearchAndFileName(searchId, runFileName);
        if (runId == 0) {
            log.error("!!!NO RUN FOUND FOR SearchId: "+searchId+"; fileName: "+runFileName);
            return 0;
        }
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
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
