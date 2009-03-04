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
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.general.impl.ExperimentBean;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.PeptideResultBuilder;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.ms2file.MS2DataUploadService;
import org.yeastrc.ms.service.sqtfile.BaseSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.ProlucidSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.SequestSQTDataUploadService;

public class MsDataUploader {

    private static final Logger log = Logger.getLogger(MsDataUploader.class);
    
    private int numRunsToUpload = 0;
    private int numRunsUploaded = 0;
    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    private int uploadedSearchId;
    private int uploadedExptId;
    private String remoteServer;
    private String remoteDirectory;
    private String uploadDirectory;
    
    private boolean isMacCossData = false;
    
    private RunFileFormat runFormat;
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    
    private static final Pattern fileNamePattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
    
    private void resetUploader() {
        uploadExceptionList.clear();
        uploadedSearchId = 0;
        numRunsToUpload = 0;
        numRunsUploaded = 0;
        numSearchesToUpload = 0;
        numSearchesUploaded = 0;
        uploadedExptId = 0;
        remoteServer = null;
        remoteDirectory = null;
        uploadDirectory = null;
        
    }
    
    public void setIsMaccossData(boolean isMaccossData) {
        this.isMacCossData = isMaccossData;
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
    
    public int getUploadedSearchId() {
        return this.uploadedSearchId;
    }
    
    public int getUploadedExperimentId() {
        return this.uploadedExptId;
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
        this.remoteServer = remoteServer;
        this.remoteDirectory = remoteDirectory;
        this.uploadDirectory = fileDirectory;
        
        logBeginExperimentUpload();
        long start = System.currentTimeMillis();
        
        // ----- BEFORE BEGINNING UPLOAD MAKE THE FOLLOWING CHECKS -----
        Set<String> filenames = getFileNamesAndDoPreUploadChecks();
        
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        // first create an entry in the msExperiment table
        this.uploadedExptId = saveExperiment();
        log.info("\n\nAdded entry for experiment ID: "+uploadedExptId+"\n\n");
        
        try {
            uploadRunAndSearchFilesToDb(filenames,searchDate);
        }
        catch (UploadException e) { // this should only result from ms2 file upload
                                    // or if unsupported or multiple types of sqt files were found.
                                    // In either case, we will delete the experiment
                                    // The raw data (MS2 files) will not be deleted
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            // delete the entry in the experiment table
            deleteExperiment();
            log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
//            numRunsUploaded = 0;
            throw e;
        }
        
        // if the last exception was because no matching search nrseq database was found, we will 
        // delete the experiment.  The raw data (MS2 files) will not be deleted
        if (uploadExceptionList.size() > 0) {
            UploadException lastException = this.uploadExceptionList.get(uploadExceptionList.size() -1);
            if (lastException.getErrorCode() == ERROR_CODE.SEARCHDB_NOT_FOUND) {
                // delete the entry in the experiment table
                deleteExperiment();
                log.error("\n\t!!!EXPERIMENT WILL NOT BE UPLOADED!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            }
        }
        
        long end = System.currentTimeMillis();
        logEndExperimentUpload(start, end);
        
        return uploadedExptId;
    }
    
    public void uploadExperimentToDb(int experimentId, String fileDirectory, Date searchDate) throws UploadException {
        resetUploader();
        MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperiment expt = exptDao.loadExperiment(experimentId);
        if (expt == null) {
            UploadException ex = new UploadException(ERROR_CODE.EXPT_NOT_FOUND);
            ex.appendErrorMessage("Experiment ID: "+experimentId+" does not exist in the database.");
            ex.appendErrorMessage("!!!EXPERIMENT WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        this.remoteServer = expt.getServerAddress();
        this.remoteDirectory = expt.getServerDirectory();
        this.uploadDirectory = fileDirectory;
        this.uploadedExptId = experimentId;
        
        logBeginExperimentUpload();
        long start = System.currentTimeMillis();
        
        // ----- BEFORE BEGINNING UPLOAD MAKE THE FOLLOWING CHECKS -----
        Set<String> filenames = getFileNamesAndDoPreUploadChecks();
        
        // ----- UPDATE THE LAST UPDATE DATE FOR THE EXPERIMENT
        updateLastUpdateDate(experimentId);
        
        // ----- DELETE ANY OLD SEARCHES UPLOADED FOR THIS EXPERIMENT
        deleteOldSearchesForExperiment(experimentId);
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        try {
            uploadRunAndSearchFilesToDb(filenames,searchDate);
        }
        catch (UploadException e) { // this should only result from ms2 file upload
            // or if unsupported sqt files were found.
            // In either case, we will delete the experiment
            // The raw data (MS2 files) will not be deleted
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            // delete the entry in the experiment table
            deleteExperiment();
            log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            // numRunsUploaded = 0;
            throw e;
        }

        // if the last exception was because no matching search nrseq database was found, we will 
        // delete the experiment.  The raw data (MS2 files) will not be deleted
        if (uploadExceptionList.size() > 0) {
            UploadException lastException = this.uploadExceptionList.get(uploadExceptionList.size() -1);
            if (lastException.getErrorCode() == ERROR_CODE.SEARCHDB_NOT_FOUND) {
                // delete the entry in the experiment table
                deleteExperiment();
                log.error("\n\t!!!EXPERIMENT WILL NOT BE UPLOADED!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            }
        }
        long end = System.currentTimeMillis();
        logEndExperimentUpload(start, end);
    }

    private void updateLastUpdateDate(int experimentId) {
        MsExperimentDAO experimentDao = DAOFactory.instance().getMsExperimentDAO();
        experimentDao.updateLastUpdateDate(experimentId);
        
    }

    private void deleteOldSearchesForExperiment(int experimentId) {
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        List<Integer> searchIds = searchDao.getSearchIdsForExperiment(experimentId);
        for (Integer id: searchIds) {
            if (id != null)
                searchDao.deleteSearch(id);
        }
    }

    private void logEndExperimentUpload(long start, long end) {
        log.info("END EXPERIMENT UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+
                "\n\tExperiment ID: "+uploadedExptId+
                "\n\t#Runs in Directory: "+numRunsToUpload+"; #Uploaded: "+numRunsUploaded+
                "\n\tSEARCH ID: "+this.uploadedSearchId+
                "\n\t#Searches in Directory: "+numSearchesToUpload+"; #Uploaded: "+numSearchesUploaded+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tUpload Directory: "+uploadDirectory+
                "\n\n");
    }

    private void logBeginExperimentUpload() {
        log.info("BEGIN EXPERIMENT UPLOAD"+
                "\n\tRemote server: "+remoteServer+
                "\n\tRemote directory: "+remoteDirectory+
                "\n\tDirectory: "+uploadDirectory+
                "\n\tTime: "+(new Date().toString()));
    }

    private Set<String> getFileNamesAndDoPreUploadChecks() throws UploadException {
        // (1). Make sure upload directory exists
        if (!(new File(uploadDirectory).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.DIRECTORY_NOT_FOUND);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // get the file names
        Set<String> filenames = getFileNamePrefixes(uploadDirectory);
        
        // (2). If we didn't find any files print warning and return.
        if (filenames.size() == 0) {
            UploadException ex = new UploadException(ERROR_CODE.EMPTY_DIRECTORY);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // (3). Get the format of the spectrum data files 
        List<RunFileFormat> runFormats = getRunFileFormat(uploadDirectory);
        if(runFormats.size() == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCAN_DATA_FORMATS);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        else if(runFormats.size() != 1) {
            UploadException ex = new UploadException(ERROR_CODE.MULTI_SCAN_DATA_FORMATS);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        this.runFormat = runFormats.get(0);
        
        // (4). We cannot upload if we do not support the scan data file format
        if(runFormat != RunFileFormat.MS2 && runFormat != RunFileFormat.CMS2) {
            UploadException ex = new UploadException(ERROR_CODE.UNSUPPORTED_SCAN_DATA_FORMAT);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        
        // (5). make sure .ms2 or .cms2 files are present
        String missingFile = missingSpectrumDataFiles(uploadDirectory, filenames, runFormat);
        if (missingFile != null) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SCAN_DATA_FILE);
            ex.setErrorMessage("Missing file: "+missingFile);
            ex.setDirectory(uploadDirectory);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        return filenames;
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
                    SearchFileFormat.SQT_PLUCID != myType &&
                    SearchFileFormat.SQT_PERC != myType) {
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
     * @param filenames
     * @return searchDate
     * @throws UploadException
     */
    private void uploadRunAndSearchFilesToDb(Set<String> filenames, Date searchDate) throws UploadException   {
       
        
        // upload the runs first. This could throw an upload exception
        Map<String, Integer> runIdMap;
        try {
            runIdMap = uploadRuns(filenames);
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\tERROR UPLOADING MS2 DATA. EXPERIMENT WILL NOT BE UPLOADED\n");
            log.error(e.getMessage(), e);
//            numRunsUploaded = 0;
            throw e;
        }

        // check if we have any sqt files.
        if (!haveSqtFiles(uploadDirectory, filenames)) {
            log.warn("\n\tNO SQT FILES FOUND TO UPLOAD. SEARCH WILL NOT BE UPLOADED\n");
            return;
        }
        
        // determine the type of sqt files we have
        // make sure there are no unsupported .sqt files. This method may throw an UploadException. 
        // We will propagate this exception
        SearchFileFormat sqtType = null;
        try {sqtType = getSqtType(uploadDirectory, filenames);}
        catch(UploadException ex) {
            ex.appendErrorMessage("\n\tSEARCH WILL NOT BE UPLOADED.");
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        
        // now upload the searches. No exception will be thrown if the upload fails
        try {
            this.uploadedSearchId =  uploadSearches(filenames, searchDate, runIdMap, sqtType);
        }
        // We should have caught all exceptions in the SQT upload classes but just in case anything slipped through...
        catch(RuntimeException e) {
            log.error("!!!ERROR UPLOADING SEARCH (RuntimeException)!!!", e);
        }
    }

    
    private boolean haveSqtFiles(String fileDirectory, Set<String> filenames) {
        
        for (String file: filenames) {
            String sqtFile = fileDirectory+File.separator+file+".sqt";
            if (new File(sqtFile).exists())
                return true;
        }
        return false;
    }

    private int saveExperiment() throws UploadException {
        MsExperimentDAO experimentDao = DAOFactory.instance().getMsExperimentDAO();
        ExperimentBean experiment = new ExperimentBean();
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        experiment.setUploadDate(new java.sql.Date(new Date().getTime()));
        try { return experimentDao.saveExperiment(experiment);}
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.CREATE_EXPT_ERROR);
            ex.appendErrorMessage("!!!\n\tERROR CREATING EXPERIMENT. EXPERIMENT WILL NOT BE UPLOADED\n!!!");
            ex.setErrorMessage(e.getMessage());
            throw ex;
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
    private Map<String, Integer> uploadRuns(Set<String> filenames) throws UploadException {
        
        MS2DataUploadService uploadService = new MS2DataUploadService();
        Map<String, Integer> runMapIds = uploadService.uploadRuns(uploadedExptId, uploadDirectory, filenames, runFormat, remoteDirectory);
        this.numRunsToUpload = uploadService.getNumRunsToUpload();
        this.numRunsUploaded = uploadService.getNumRunsUploaded();
        this.uploadExceptionList.addAll(uploadService.getUploadExceptionList());
        return runMapIds;
    }

    /**
     * @param filenames
     * @param searchDate
     * @param runIdMap
     * @param sqtType
     */
    private int uploadSearches(Set<String> filenames,Date searchDate,
            Map<String, Integer> runIdMap, SearchFileFormat sqtType) {
        
        // upload the search
        if (sqtType == SearchFileFormat.SQT_SEQ || sqtType == SearchFileFormat.SQT_NSEQ) {
            return uploadSequestSearch(filenames, runIdMap, sqtType, searchDate);
        }
        else if (sqtType == SearchFileFormat.SQT_PLUCID) {
            return uploadProlucidSearch(filenames, runIdMap, searchDate);
        }
        else if(sqtType == SearchFileFormat.SQT_PERC) {
            return uploadPercolatorSearch(filenames, runIdMap, searchDate);
        }
        else {
            log.error("Unknow SQT type");
            return 0;
        }
    }
    
    // upload sequest sqt files
    private int uploadSequestSearch(Set<String> filenames, Map<String, Integer> runIdMap, 
            SearchFileFormat sqtType, final Date searchDate) {
        
        Program program = Program.programForFileFormat(sqtType);
        SequestSQTDataUploadService service = new SequestSQTDataUploadService(program);
        // If this is data from the MacCoss lab we will check for "duplicate" results for
        // a scan+charge combination.
        if(isMacCossData)
            service.doScanChargeMassCheck(true);
        
        int searchId = service.uploadSearch(uploadedExptId, uploadDirectory, filenames, runIdMap, 
                                        remoteServer, remoteDirectory, 
                                        new java.sql.Date(searchDate.getTime()));
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
        return searchId;
    }

    // upload prolucid sqt files
    private int uploadProlucidSearch(Set<String> filenames, Map<String, Integer> runIdMap,Date searchDate) {
        ProlucidSQTDataUploadService service = new ProlucidSQTDataUploadService();
        int searchId = service.uploadSearch(uploadedExptId, uploadDirectory, filenames, runIdMap, 
                                    remoteServer, remoteDirectory, 
                                    new java.sql.Date(searchDate.getTime()));
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
        return searchId;
    }
    
    // upload percolator sqt files
    private int uploadPercolatorSearch(Set<String> filenames, Map<String, Integer> runIdMap, final Date searchDate) {
        
        // first upload the sqt files to populate the core search tables
        SearchParamsDataProvider paramsProvider = new SequestParamsParser();
        PeptideResultBuilder peptbuilder = SequestResultPeptideBuilder.instance();
        
        BaseSQTDataUploadService service = new BaseSQTDataUploadService(paramsProvider, peptbuilder, Program.PERCOLATOR);
        if(isMacCossData) 
            service.doScanChargeMassCheck(true);
        
        int searchId = service.uploadSearch(uploadedExptId, uploadDirectory, filenames, runIdMap, 
                        remoteServer, remoteDirectory, new java.sql.Date(searchDate.getTime()));
        
        this.uploadExceptionList.addAll(service.getUploadExceptionList());
        this.numSearchesToUpload = service.getNumSearchesToUpload();
        this.numSearchesUploaded = service.getNumSearchesUploaded();
        
        // if the search information could not be uploaded don't go any further
        if(uploadExceptionList.size() > 0) {
            return searchId;
        }
        
        // now upload the Percolator search results
        PercolatorSQTDataUploadService percService = new PercolatorSQTDataUploadService();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);
        Map<String, Integer> runSearchIdMap = new HashMap<String, Integer>(runSearchIds.size());
        for(Integer id: runSearchIds) {
            String filename = runSearchDao.loadFilenameForRunSearch(id);
            runSearchIdMap.put(filename, id);
        }
        percService.uploadPostSearchAnalysis(searchId, Program.SEQUEST, uploadDirectory, filenames, 
                runSearchIdMap, remoteDirectory);
        
        this.uploadExceptionList.addAll(percService.getUploadExceptionList());
        
        return searchId;
    }
    
    /**
     * Check for .ms2 or .cms2 files. 
     * @param fileDirectory
     * @param filenames
     * @return
     */
    private String missingSpectrumDataFiles(String fileDirectory, Set<String> filenames, RunFileFormat format) {
        
        for (String filePrefix: filenames) {
            if (!(new File(fileDirectory+File.separator+filePrefix+"."+format.name().toLowerCase()).exists())  ||
                !(new File(fileDirectory+File.separator+filePrefix+"."+format.name().toUpperCase()).exists())) {
                log.error("Required file: "+filePrefix+"."+format.name().toLowerCase()+" not found");
                return filePrefix+"."+format.name().toLowerCase();
            }
        }
        return null;
    }
    
    private List<RunFileFormat> getRunFileFormat(String fileDirectory) {
        
        File directory = new File (fileDirectory);
        File[] files = directory.listFiles();
        
        Map<String, RunFileFormat> formats = new HashMap<String, RunFileFormat>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            String fileName = files[i].getName();
            String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            if(RunFileFormat.isSupportedFormat(ext)) {
                RunFileFormat fmt = RunFileFormat.instance(ext);
                formats.put(fmt.name(), fmt);
            }
        }
        return new ArrayList<RunFileFormat>(formats.values());
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
            if(files[i].isDirectory())
                continue;
            String fileName = files[i].getName();
            String fileName_LC = fileName.toLowerCase();
            if (fileName_LC.endsWith(".ms2") || fileName_LC.endsWith(".cms2") || fileName_LC.endsWith(".sqt")) {
                name = files[i].getName();
                name = name.substring(0, name.indexOf('.'));
                filenames.add(name);
            }
        }
        return filenames;
    }
    
    private void deleteExperiment() {
        log.error("\n\tDELETING EXPERIMENT: "+uploadedExptId);
        MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
        exptDao.deleteExperiment(uploadedExptId);
        this.uploadedExptId = 0;
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

//        for(int i = 0; i < 10; i++) {
        MsDataUploader uploader = new MsDataUploader();
        
        String directory = args[0];
        if(directory == null || directory.length() == 0 || !(new File(directory).exists()))
            System.out.println("Invalid directory: "+directory);
        
        boolean maccossData = Boolean.parseBoolean(args[1]);
        uploader.setIsMaccossData(maccossData);
        
        System.out.println("Directory: "+directory+"; Maccoss Data: "+maccossData);
        
        uploader.uploadExperimentToDb("local",
                directory,
                directory,
                new Date());
//        }
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
