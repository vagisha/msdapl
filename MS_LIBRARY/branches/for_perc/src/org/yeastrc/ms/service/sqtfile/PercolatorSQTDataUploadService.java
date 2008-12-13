/**
 * PercolatorSQTDataUploadService.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorOutputDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorSearchScan;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorOutputBean;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.percolator.PercolatorSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class PercolatorSQTDataUploadService {
    
    
    private static final Logger log = Logger.getLogger(PercolatorSQTDataUploadService.class);

    private static final DAOFactory daoFactory = DAOFactory.instance();
    private static final MsSearchResultDAO resultDao = daoFactory.getMsSearchResultDAO();

    private static final int BUF_SIZE = 1000;

    private List<? extends MsResidueModificationIn> dynaResidueMods;
    private List<? extends MsTerminalModificationIn> dynaTermMods;
    
    // these are the things we will cache and do bulk-inserts
    private List<PercolatorResultDataWId> percolatorResultDataList; // percolator scores


    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();

    private int numAnalysisToUpload = 0;
    private int numAnalysisUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msPostSearchAnalysis
    private String programVersion = "uninit";

    int lastUploadedPercOutputId;
    private int analysisId;
    
    public PercolatorSQTDataUploadService() {
        this.uploadExceptionList = new ArrayList<UploadException>();
        this.percolatorResultDataList = new ArrayList<PercolatorResultDataWId>(BUF_SIZE);
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
    }
    
    void reset() {

        analysisId = 0;
        
        numAnalysisToUpload = 0;
        numAnalysisUploaded = 0;

        resetCaches();

        uploadExceptionList.clear();
        programVersion = "uninit";
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    // called before uploading each sqt file and in the reset() method.
    void resetCaches() {
        percolatorResultDataList.clear();
        lastUploadedPercOutputId = 0;
    }

    public final List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }

    public final int getNumAnalysisToUpload() {
        return numAnalysisToUpload;
    }

    public final int getNumAnalysisUploaded() {
        return numAnalysisUploaded;
    }


    public static int getNumFilesToUpload(String fileDirectory, Set<String> fileNames) {
        int num = 0;
        for (String file: fileNames) {
            if ((new File(fileDirectory+File.separator+file+".sqt")).exists())
                num++;
        }
        return num;
    }

    public static void updateProgramVersion(int analysisId, String programVersion) {
        try {
            MsSearchAnalysisDAO analysisDao = daoFactory.getPostSearchAnalysisDAO();
            analysisDao.updateAnalysisProgramVersion(analysisId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for analysisID: "+analysisId, e);
        }
    }

    /**
     * @param fileDirectory 
     * @param fileNames names of sqt files (without the .sqt extension)
     * @param runIdMap map mapping file names to runIds in database
     * @param remoteServer 
     * @param remoteDirectory 
     * @param searchDate 
     */
    public int uploadPostSearchAnalysis(int searchId, String fileDirectory, Set<String> fileNames,
            Map<String,Integer> runSearchIdMap, String remoteDirectory) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numAnalysisToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        // create a new entry in the msPostSearchAnalysis table
        try {
            analysisId = saveTopLevelAnalysis(searchId, remoteDirectory);
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            return 0;
        }
        
        
        // now upload the individual sqt files
        for (String file: fileNames) {
            String filePath = fileDirectory+File.separator+file+".sqt";
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            Integer runSearchId = runSearchIdMap.get(file); 
            if (runSearchId == null) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_SQT);
                ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...\n");
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                deleteAnalysis(searchId);
                numAnalysisUploaded = 0;
                return 0;
            }
            resetCaches();
            try {
                uploadSqtFile(filePath, runSearchId);
                numAnalysisUploaded++;
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...\n");
                log.error(ex.getMessage(), ex);
                deleteAnalysis(searchId);
                numAnalysisUploaded = 0;
                return 0;
            }
        }
        
        // if no sqt files were uploaded delete the top level search
        if (numAnalysisUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_PERC_ANALYSIS_UPLOADED);
            uploadExceptionList.add(ex);
            ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...\n");
            log.error(ex.getMessage(), ex);
            deleteAnalysis(searchId);
            numAnalysisUploaded = 0;
            return 0;
        }
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (this.programVersion != null && this.programVersion != "uninit") {
            updateProgramVersion(searchId, programVersion);
        }
        
        return analysisId;
    }
    
    private void getSearchModifications(int searchId) {
       MsSearch search = daoFactory.getMsSearchDAO().loadSearch(searchId);
       this.dynaResidueMods = search.getDynamicResidueMods();
       this.dynaTermMods = search.getDynamicTerminalMods();
    }

    private int saveTopLevelAnalysis(int searchId, String remoteDirectory) throws UploadException {
        MsSearchAnalysisDAO analysisDao = daoFactory.getPostSearchAnalysisDAO();
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setSearchId(searchId);
        analysis.setServerDirectory(remoteDirectory);
        analysis.setAnalysisProgram(SearchProgram.PERCOLATOR);
        try {
            return analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private void uploadSqtFile(String filePath, int runSearchId) throws UploadException {
        
        log.info("BEGIN PERCOLATOR SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_SEARCH_ID: "+runSearchId);
        lastUploadedPercOutputId = 0;
        
        long startTime = System.currentTimeMillis();
        PercolatorSQTFileReader provider = new PercolatorSQTFileReader();
        
        try {
            provider.open(filePath);
            provider.setDynamicResidueMods(this.dynaResidueMods);
            provider.setDynamicTerminalMods(this.dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        try {
            uploadPercolatorSqtFile(provider, runSearchId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            ex.appendErrorMessage("\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END PERCOLATOR SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_SEARCH_ID: "+runSearchId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
    }
    
    // parse and upload a sqt file
    private void uploadPercolatorSqtFile(PercolatorSQTFileReader provider, int runSearchId) throws UploadException {
        
        try {
            lastUploadedPercOutputId = uploadPercolatorHeader(provider, runSearchId, analysisId);
            log.info("Uploaded top-level info for Percolator sqt file. percOutputId: "+lastUploadedPercOutputId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }

        // get the runID. Will be needed later to get the scan number
        int runId = getRunIdForRunSearch(runSearchId);
        if(runId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SQT);
            ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
            throw ex;
        }
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        while (provider.hasNextSearchScan()) {
            PercolatorSearchScan scan = null;
            try {
                scan = provider.getNextSearchScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            // save all the search results for this scan
            for (PercolatorResultIn result: scan.getScanResults()) {
                uploadSearchResult(result, lastUploadedPercOutputId, runSearchId, runId);
                numResults++;
            }
        }
        flush(); // save any cached data
        log.info("Uploaded Percolator SQT file: "+provider.getFileName()+", with "+numResults+
                " results. (percOutputId: "+lastUploadedPercOutputId+")");
                
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
        MsRunSearch rs = rsDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    

    // PERCOLATOR SQT HEADER
    private final int uploadPercolatorHeader(SQTSearchDataProvider provider, int runSearchId, int percId)
        throws DataProviderException {

        SQTRunSearchIn search = provider.getSearchHeader();
        if (search instanceof SQTHeader) {
            SQTHeader header = (SQTHeader)search;
            
            // SEARCH PROGRAM VERSION IN THE SQT FILE
            // this is the first time we are assigning a value to program version
            if ("uninit".equals(programVersion))
                this.programVersion = header.getSearchEngineVersion();

            // make sure the SQTGeneratorVersion value is same in all sqt headers
            if (programVersion == null) {
                if (header.getSearchEngineVersion() != null)
                    throw new DataProviderException("Value of Percolator version is not the same in all SQT files.");
            }
            else if (!programVersion.equals(header.getSearchEngineVersion())) {
                throw new DataProviderException("Value of Percolator version is not the same in all SQT files.");
            }
            
        }
        // save the percolator and return the database id
        PercolatorOutputDAO percOutputDao = daoFactory.getPercolatorOutputDAO();
        PercolatorOutputBean output = new PercolatorOutputBean();
        output.setPercolatorId(percId);
        output.setRunSearchId(runSearchId);
        output.setAnalysisFileFormat(SearchFileFormat.SQT_PERC);
        int percOutputId = percOutputDao.save(output);
        
        // save all the headers
        PercolatorParamsDAO headerDao = daoFactory.getPercoltorSQTHeaderDAO();
        for(SQTHeaderItem header: search.getHeaders()) {
            headerDao.saveParam(header, percOutputId);
        }
        
        return percOutputId;
    }

    private void uploadSearchResult(PercolatorResultIn result, int percOutputId, int runSearchId, int runId) throws UploadException {
        
        // get a matching search result for the Percolator result
        int scanId = getScanId(runId, result.getScanNumber());
        MsSearchResult searchResult = null;
        try {
        resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                    result.getCharge(), 
                    result.getResultPeptide().getPeptideSequence());
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.NOT_MATCHING_SEARCH_RESULT, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(searchResult == null) {
            UploadException ex = new UploadException(ERROR_CODE.NOT_MATCHING_SEARCH_RESULT);
            ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                    " scanId: "+scanId+"; charge: "+result.getCharge()+
                    "; peptide: "+result.getResultPeptide().getPeptideSequence());
            throw ex;
        }
        
        // upload the Percolator specific information for this result.
        uploadPercolatorResultData(result, percOutputId, searchResult.getId());
    }

    private void uploadPercolatorResultData(PercolatorResultIn resultData, int percOutputId, int resultId) {
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorResultDataList.size() >= BUF_SIZE) {
            uploadPercolatorResultBuffer();
        }
        // add the Percolator specific information for this result to the cache
        PercolatorResultDataBean res = new PercolatorResultDataBean();
        res.setPercolatorOutputId(percOutputId);
        res.setResultId(resultId);
        res.setDiscriminantScore(resultData.getDiscriminantScore());
        res.setPosteriorErrorProbability(resultData.getPosteriorErrorProbability());
        res.setQvalue(resultData.getQvalue());
        percolatorResultDataList.add(res);
    }
    
    private void uploadPercolatorResultBuffer() {
        PercolatorResultDAO percResultDao = daoFactory.getPercolatorResultDAO();
        percResultDao.saveAllPercolatorResultData(percolatorResultDataList);
        percolatorResultDataList.clear();
    }
    
    private void flush() {
        if (percolatorResultDataList.size() > 0) {
            uploadPercolatorResultBuffer();
        }
    }
    

//    final void deleteLastUploadedAnalysis() {
//        if (lastUploadedPercOutputId == 0)
//            return;
//        PercolatorOutputDAO outputDao = daoFactory.getPercolatorOutputDAO();
//        outputDao.delete(lastUploadedPercOutputId);
//    }

    static int getScanId(int runId, int scanNumber) throws UploadException {

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    public static void deleteAnalysis(int analysisId) {
        if (analysisId == 0)
            return;
        MsSearchAnalysisDAO analysisDao = daoFactory.getPostSearchAnalysisDAO();
        analysisDao.delete(analysisId);
    }
    
}
