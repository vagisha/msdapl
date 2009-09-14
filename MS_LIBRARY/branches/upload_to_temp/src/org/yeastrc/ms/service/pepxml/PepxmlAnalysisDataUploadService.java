package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetAnalysisBean;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.InteractPepXmlFileReader;
import org.yeastrc.ms.service.AnalysisDataUploadService;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.analysis.MsRunSearchAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.MsSearchAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetResultUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetRocUploadDAO;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;

// This will upload the corresponding PeptideProphet results. 
public class PepxmlAnalysisDataUploadService implements AnalysisDataUploadService {

    private static final int BUF_SIZE = 500;
    
    private int searchId;
    private List<Integer> analysisIds;
    
    private String dataDirectory;
    private String remoteServer;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    
    private List<String> searchDataFileNames;
    private List<String> interactPepxmlFiles;

    private boolean preUploadCheckDone;
    
    private final MsScanUploadDAO scanDao;
    private final MsRunSearchUploadDAO runSearchDao;
    private final MsSearchResultUploadDAO resultDao;
    private final MsSearchUploadDAO searchDao;
    private final MsSearchAnalysisUploadDAO analysisDao;
    private final MsRunSearchAnalysisUploadDAO runSearchAnalysisDao;
    private final PeptideProphetRocUploadDAO rocDao;
    private final PeptideProphetResultUploadDAO ppResDao;
    private final PeptideProphetAnalysisUploadDAO ppAnalysisDao;
    
    
    // these are the things we will cache and do bulk-inserts
    private List<PeptideProphetResultDataWId> prophetResultDataList; // PeptideProphet scores
    
    private List<MsResidueModification> dynaResidueMods;
    private List<MsTerminalModification> dynaTermMods;
    
    private int numAnalysisUploaded = 0;
    
    private StringBuilder uploadMsg;
    
    private static final Pattern fileNamePattern = Pattern.compile("interact*.pep.xml");
    
    private static final Logger log = Logger.getLogger(PepxmlAnalysisDataUploadService.class.getName());
    
    public PepxmlAnalysisDataUploadService() {
        
        this.searchDataFileNames = new ArrayList<String>();
        this.interactPepxmlFiles = new ArrayList<String>();
        
        this.prophetResultDataList = new ArrayList<PeptideProphetResultDataWId>(BUF_SIZE);
        
        this.dynaResidueMods = new ArrayList<MsResidueModification>();
        this.dynaTermMods = new ArrayList<MsTerminalModification>();
        
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        
        this.scanDao = daoFactory.getMsScanDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.resultDao = daoFactory.getMsSearchResultDAO();
        
        this.analysisDao = daoFactory.getMsSearchAnalysisDAO();
        this.runSearchAnalysisDao  = daoFactory.getMsRunSearchAnalysisDAO();
        
        this.rocDao = daoFactory.getPeptideProphetRocDAO();
        this.ppResDao = daoFactory.getPeptideProphetResultDAO();
        this.ppAnalysisDao = daoFactory.getPeptideProphetAnalysisDAO();
        
        uploadMsg = new StringBuilder();
    }
    

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }
    

    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        return "\tSearch file format: "+SearchFileFormat.PEPXML+
                uploadMsg.toString();
    }

    @Override
    public boolean preUploadCheckPassed() {
        
        preUploadCheckMsg = new StringBuilder();
        
        // checks for
        // 1. valid data directory
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            appendToMsg("Data directory does not exist: "+dataDirectory);
            return false;
        }
        if(!dir.isDirectory()) {
            appendToMsg(dataDirectory+" is not a directory");
            return false;
        }
        
        // 2. Look for interact*.pep.xml file
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".pep.xml");
            }});
        
        boolean found = false;
        for (int i = 0; i < files.length; i++) {
            if (fileNamePattern.matcher(files[i].getName().toLowerCase()).matches()) {
                  interactPepxmlFiles.add(files[i].getName());
                  found = true;
              }
        }
        if(!found) {
            appendToMsg("Could not find interact*.pep.xml file(s) in directory: "+dataDirectory);
            return false;
        }
        
        // 3. If we know the search data file names that were uploaded match them with up with the 
        //    file names in the interact*.pep.xml file(s) 
        for(String file: interactPepxmlFiles) {
            InteractPepXmlFileReader parser = new InteractPepXmlFileReader();
            try {
                parser.open(dataDirectory+File.separator+file);
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+file+"\n"+e.getMessage());
                return false;
            }
            List<String> inputFileNames = new ArrayList<String>();
            try {
                while(parser.hasNextRunSearch()) {
                    inputFileNames.add(parser.getFileName());
                }
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+file+"\n"+e.getMessage());
                return false;
            }
            parser.close();
        
            if(searchDataFileNames != null) {
                for(String input:inputFileNames) {
                    if(!searchDataFileNames.contains(input)) {
                        appendToMsg("No corresponding search data file found for: "+input);
                        return false;
                    }
                }
            }
        }
        
        preUploadCheckDone = true;
        
        return true;
    }

    
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

    @Override
    public void upload() throws UploadException {
        
        reset();// reset all caches etc.
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!PEPTIDE_PROPHET ANALYSIS WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        Map<String,Integer> runSearchIdMap;
        try {
           runSearchIdMap = createRunSearchIdMap();
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\t!!!PEPTIDE_PROPHET ANALYSIS WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        // now upload the analysis (PeptideProphet) data in the interact*.pep.xml file(s)
        for(String file: interactPepxmlFiles) {
            
            numAnalysisUploaded = 0;
            
            // determine if a file with this name has already been uploaded for this experiment
            PeptideProphetAnalysis ppAnalysis = ppAnalysisDao.loadAnalysisForFileName(file, searchId);
            if(ppAnalysis != null) {
                log.info("Analysis file: "+file+" has already been uploaded. AnalysisID: "+ppAnalysis.getId());
                this.analysisIds.add(ppAnalysis.getId());
                continue;
            }
            
            // file has not been uploaded upload it now
            log.info("Uploading interact pepxml file: "+file);
            InteractPepXmlFileReader parser = new InteractPepXmlFileReader();
            try {
                parser.open(dataDirectory+File.separator+file);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                throw ex;
            }

            // TODO  If refresh parser has not been run determine peptide protein matches from the 
            // protein sequence database.
            if(!parser.isRefreshParserRun()) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR);
                ex.setErrorMessage("Refresh parser has not been run");
                throw ex;
            }

            // create a new entry for PeptideProphet analysis.
            int analysisId = 0;
            try {
                analysisId = createPeptideProphetAnalysis(parser, file);
            }
            catch(UploadException ex) {
                ex.appendErrorMessage("\n\tANALYSIS WILL NOT BE UPLOADED..."+file+"\n");
                throw ex;
            }

            try {
                while(parser.hasNextRunSearch()) {
                    String filename = parser.getFileName();

                    Integer runSearchId = runSearchIdMap.get(filename);
                    try {
                        uploadRunSearchAnalysis(filename, searchId, analysisId, runSearchId, parser);
                    }
                    catch (UploadException ex) {
                        ex.appendErrorMessage("\n\tDELETING ANALYSIS..."+analysisId+"\n");
                        deleteAnalysis(analysisId);
                        throw ex;
                    }
                    numAnalysisUploaded++;

                    resetCaches();
                }
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING ANALYSIS..."+analysisId+"\n");
                deleteAnalysis(analysisId);
                throw ex;
            }
            finally {
                parser.close();
            }


            // if no PeptideProphet analyses were uploaded delete the top level search analysis
            if (numAnalysisUploaded == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_PEPTPROPH_ANALYSIS_UPLOADED);
                ex.appendErrorMessage("\n\tDELETING PEPTIDE PROPHET ANALYSIS...ID: "+analysisId+"\n");
                deleteAnalysis(analysisId);
                numAnalysisUploaded = 0;
                throw ex;
            }
            
            uploadMsg.append("\n\t#Analyses in "+file+" : "+numAnalysisUploaded);
            this.analysisIds.add(analysisId);
        }
    }
    
    private void getSearchModifications(int searchId) {
        MsSearch search = searchDao.loadSearch(searchId);
        this.dynaResidueMods = search.getDynamicResidueMods();
        this.dynaTermMods = search.getDynamicTerminalMods();
     }
    
    // ---------------------------------------------------------------------------------------------
    // SAVE THE PEPTIDE PROPHET ANALYSIS INFORMATION
    // ---------------------------------------------------------------------------------------------
    private int createPeptideProphetAnalysis(InteractPepXmlFileReader parser, String pepxmlFile) throws UploadException {
            
        PeptideProphetAnalysisBean analysis = new PeptideProphetAnalysisBean();
//      analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PEPTIDE_PROPHET);
        analysis.setAnalysisProgramVersion(parser.getPeptideProphetVersion());
        analysis.setFileName(pepxmlFile);
        int analysisId;
        try {
            analysisId = analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        PeptideProphetROC roc = parser.getPeptideProphetRoc();
        roc.setSearchAnalysisId(analysisId);
        rocDao.saveRoc(roc);
        
        analysis.setId(analysisId);
        ppAnalysisDao.save(analysis);
        
        return analysisId;
        
    }

    private int getScanId(int runId, int scanNumber)
            throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    
    private void uploadRunSearchAnalysis(String filename, int searchId, int analysisId, int runSearchId,
            InteractPepXmlFileReader parser) throws UploadException {
        
        int runSearchAnalysisId = uploadRunSearchAnalysis(analysisId, runSearchId);
        int runId = getRunIdForRunSearch(runSearchId);
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                PepXmlSearchScanIn scan = parser.getNextSearchScan();
                
                int scanId = getScanId(runId, scan.getScanNumber());
                
                for(SequestPeptideProphetResultIn result: scan.getScanResults()) {
                    int resultId = getUploadedResultId(result, runSearchId, scanId);
                    uploadAnalysisResult(result, resultId, runSearchAnalysisId);      // PeptideProphet scores
                    numResults++;
                }
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            throw ex;
        }
        
        flush(); // save any cached data
        log.info("Uploaded analysis results for file: "+filename+", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");
        
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    
    private int getUploadedResultId(SequestPeptideProphetResultIn result, int runSearchId, int scanId) throws UploadException {
        
        MsSearchResult searchResult = null;
        try {
            List<MsSearchResult> matchingResults = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                        result.getCharge(), 
                        result.getResultPeptide().getPeptideSequence());
            
            if(matchingResults.size() == 1) 
                searchResult = matchingResults.get(0);
            
            else if(matchingResults.size() > 1) { // this can happen if we have the same sequence with different mods
                String myPeptide = result.getResultPeptide().getModifiedPeptide();
                for(MsSearchResult res: matchingResults) {
                    if(myPeptide.equals(res.getResultPeptide().getModifiedPeptide())) {
                        if(searchResult != null) {
                            UploadException ex = new UploadException(ERROR_CODE.MULTI_MATCHING_SEARCH_RESULT);
                            ex.setErrorMessage("Multiple matching search results were found for runSearchId: "+runSearchId+
                                    " scanId: "+scanId+"; charge: "+result.getCharge()+
                                    "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                                    "; modified peptide: "+result.getResultPeptide().getModifiedPeptidePS());
                            throw ex;
                        }
                        searchResult = res;
                    }
                }
            }
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        catch (ModifiedSequenceBuilderException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(searchResult == null) {
            UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
            ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                    " scanId: "+scanId+"; charge: "+result.getCharge()+
                    "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                    "; modified peptide: "+result.getResultPeptide().getModifiedPeptidePS());
            throw ex;
        }
        return searchResult.getId();
    }

    
    private void flush() {
        
        if(prophetResultDataList.size() > 0) {
            uploadPeptideProphetResultBuffer();
        }
    }
    // -------------------------------------------------------------------------------------------
    // UPLOAD PEPTIDE PROPHET ANALYSIS RESULT
    // -------------------------------------------------------------------------------------------
    private boolean uploadAnalysisResult(SequestPeptideProphetResultIn result, int resultId, int rsAnalysisId) 
        throws UploadException {
        
        
        // upload the PeptideProphet specific result information if the cache has enough entries
        if (prophetResultDataList.size() >= BUF_SIZE) {
            uploadPeptideProphetResultBuffer();
        }
        
        // add the PeptideProphet specific information for this result to the cache
        PeptideProphetResultDataBean res = new PeptideProphetResultDataBean();
        res.setRunSearchAnalysisId(rsAnalysisId);
        res.setResultId(resultId);
        res.setProbability(result.getProbability());
        res.setfVal(result.getfVal());
        res.setNumEnzymaticTermini(result.getNumEnzymaticTermini());
        res.setNumMissedCleavages(result.getNumMissedCleavages());
        res.setMassDifference(result.getMassDifference());
        res.setProbabilityNet_0(result.getProbabilityNet_0());
        res.setProbabilityNet_1(result.getProbabilityNet_1());
        res.setProbabilityNet_2(result.getProbabilityNet_2());
       
        prophetResultDataList.add(res);
        return true;
    }

    private void uploadPeptideProphetResultBuffer() {
        ppResDao.saveAllPeptideProphetResultData(prophetResultDataList);
        prophetResultDataList.clear();
    }
    
    // -------------------------------------------------------------------------------
    // UPLOAD DATA INTO THE msRunSearchAnalysis TABLE
    // -------------------------------------------------------------------------------
    private int uploadRunSearchAnalysis(int analysisId, int runSearchId) 
        throws UploadException {
        
        // TODO save any PeptideProphet params?
        // save the run search analysis and return the database id
        RunSearchAnalysisBean rsa = new RunSearchAnalysisBean();
        rsa.setAnalysisFileFormat(SearchFileFormat.PEPXML);
        rsa.setAnalysisId(analysisId);
        rsa.setRunSearchId(runSearchId);
        return runSearchAnalysisDao.save(rsa);
    }

    private Map<String, Integer> createRunSearchIdMap() throws UploadException {
        
        Map<String, Integer> runSearchIdMap = new HashMap<String, Integer>(searchDataFileNames.size()*2);
        
        for(String file: searchDataFileNames) {
            int runSearchId = runSearchDao.loadIdForSearchAndFileName(searchId, file);
            if(runSearchId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
                ex.appendErrorMessage("File: "+file);
                ex.appendErrorMessage("; SearchID: "+searchId);
                throw ex;
            }
            runSearchIdMap.put(file, runSearchId);
        }
        return runSearchIdMap;
    }


    void reset() {

        numAnalysisUploaded = 0;

        resetCaches();

        searchId = 0;
        analysisIds.clear();
        
        preUploadCheckMsg = new StringBuilder();
        uploadMsg = new StringBuilder();
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    // called before uploading each msms_run_search in the interact.pep.xml file and in the reset() method.
    private void resetCaches() {
        
        prophetResultDataList.clear();
    }
    
    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return SearchFileFormat.PEPXML;
    }

    @Override
    public void setSearchDataFileNames(List<String> searchDataFileNames) {
        this.searchDataFileNames = searchDataFileNames;
    }

    @Override
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    @Override
    public void setSearchProgram(Program searchProgram) {
        throw new UnsupportedOperationException();
    }
    
    public void deleteAnalysis(int analysisId) {
        if (analysisId == 0)
            return;
        log.info("Deleting analysis ID: "+analysisId);
        analysisDao.delete(analysisId);
    }


    @Override
    public List<Integer> getUploadedAnalysisIds() {
        return this.analysisIds;
    }
}
