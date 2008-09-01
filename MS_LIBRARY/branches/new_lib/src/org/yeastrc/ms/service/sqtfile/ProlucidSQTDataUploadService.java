/**
 * ProlucidSQTDataUploadService.java
 * @author Vagisha Sharma
 * Aug 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.PrimaryScore;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.SecondaryScore;
import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class ProlucidSQTDataUploadService extends AbstractSQTDataUploadService {

private static final String PROLUCID_PARAMS_FILE = "search.xml";
    
    List<ProlucidResultDataDb> prolucidResultDataList; // prolucid scores
    
    
    public ProlucidSQTDataUploadService() {
        super();
        this.prolucidResultDataList = new ArrayList<ProlucidResultDataDb>();
    }
    
    void resetCaches() {
        super.resetCaches();
        prolucidResultDataList.clear();
    }
    
    /**
     * @param fileDirectory 
     * @param fileNames names of sqt files (without the .sqt extension)
     * @param runIdMap map mapping file names to runIds in database
     * @param remoteServer 
     * @param remoteDirectory 
     * @param searchDate 
     * @return searchId
     * @throws UploadException 
     */
    public int uploadProlucidSearch(String fileDirectory, Set<String> fileNames, Map<String,Integer> runIdMap, String remoteServer, String remoteDirectory, Date searchDate) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numSearchesToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        // parse the parameter file 
        ProlucidParamsParser parser = parseProlucidParams(fileDirectory, remoteServer);
        if (parser == null)
            return 0;
        
        // dynamic residue mods found in the params file
        List<MsResidueModification> dynaResMods = parser.getDynamicResidueMods();
        // dynamic terminal mods found in the params file
        List<MsTerminalModification> dynaTermMods = parser.getDynamicTerminalMods();
        
        // primary score type used for the search 
        PrimaryScore primaryScoreType = parser.getPrimaryScoreType();
        // secondary score type used for the search
        SecondaryScore secondaryScoreType = parser.getSecondaryScoreType();
        
        
        // database used for the search (will be used to look up protein ids later)
        String searchDbName = parser.getSearchDatabase().getServerPath();
        
        // Upload to-level search data
        int searchId = uploadSearchParams(parser, remoteServer, remoteDirectory, searchDate);
        // if an error happened while parsing or uploading the parameters file don't go any further.
        if (searchId == 0)
            return 0;
        
        // initialize the Modification lookup map
        dynaModLookup = new DynamicModLookupUtil(searchId);
        
        // now upload the individual sqt files
        for (String file: fileNames) {
            String filePath = fileDirectory+File.separator+file+".sqt";
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            Integer runId = runIdMap.get(file); 
            if (runId == null) {
                log.error("No runId for sqt file: "+file); // this should never happen
                continue;
            }
            // Consume any exceptions during parsing and upload of a sqt file. If exceptions occur, this search will be deleted
            // but the rest of the upload will continue.
            uploadProlucidSqtFile(remoteServer, filePath, searchId, runId, dynaResMods, dynaTermMods, searchDbName, primaryScoreType, secondaryScoreType);
        }
        
        // if no sqt files were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            deleteSearch(searchId);
            searchId = 0;
        }
        
        // Update the "sequenceLength" and "proteinCount" columns in the msSequenceDataDetail table
        // TODO really??
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (programVersion != null && !("uninit".equals(programVersion))) {
            updateProgramVersion(searchId, programVersion);
        }
        
        return searchId;
    }
    
    private ProlucidParamsParser parseProlucidParams(String fileDirectory, final String remoteServer) {
        
        log.info("BEGIN ProLuCID search upload -- parsing search.xml");
        String paramFile = fileDirectory+File.separator+PROLUCID_PARAMS_FILE;
        if (!(new File(paramFile).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_PROLUCID_PARAMS);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return null;
        }
        // parse the parameters file
        final ProlucidParamsParser parser = new ProlucidParamsParser();
        try {
            parser.parseParamsFile(remoteServer, paramFile);
            return parser;
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(paramFile);
            ex.setErrorMessage(e.getMessage());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return null;
        }
    }
    
    // parse and upload data from the search.xml file
    private int uploadSearchParams(ProlucidParamsParser parser, final String remoteServer, 
            final String remoteDirectory, final Date searchDate) {
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MsSearchDAO<ProlucidSearch, ProlucidSearchDb> searchDAO = DAOFactory.instance().getProlucidSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, remoteServer, remoteDirectory, searchDate));
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return 0;
        }
    }

    // Consume any exceptions during parsing and upload of a sqt file. If exceptions occur, this search will be deleted
    // but the experiment upload will continue.
    private void uploadProlucidSqtFile(final String remoteServer, String filePath, 
            int searchId, int runId, 
            List<MsResidueModification> dynaResMods, List<MsTerminalModification> dynaTermMods,
            String searchDbName, 
            PrimaryScore primaryScoreType, SecondaryScore secondaryScoreType) {
        
        resetCaches();
        
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        lastUploadedRunSearchId = 0;
        long startTime = System.currentTimeMillis();
        ProlucidSQTFileReader provider = new ProlucidSQTFileReader();
        
        try {
            provider.open(filePath, remoteServer, primaryScoreType, secondaryScoreType);
            provider.setDynamicResidueMods(dynaResMods);
            provider.setDynamicTerminalMods(dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        
        try {
            uploadProlucidSqtFile(provider, searchId, runId, searchDbName);
        }
        catch (UploadException e) {
            deleteLastUploadedRunSearch(); // if something was uploaded delete it
            e.setFile(filePath);
            String msg = e.getErrorMessage() == null ? "" : e.getErrorMessage();
            e.setErrorMessage(msg+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            return;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            deleteLastUploadedRunSearch(); // if something was uploaded delete it
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        
        numSearchesUploaded++;
    }
    
    // parse and upload a sqt file
    private void uploadProlucidSqtFile(ProlucidSQTFileReader provider, int searchId, int runId, String searchDbName) throws UploadException {
        
        try {
            lastUploadedRunSearchId = uploadSearchHeader(provider, runId, searchId);
            log.info("Uploaded top-level info for sqt file. runSearchId: "+lastUploadedRunSearchId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }

        // upload the search results for each scan + charge combination
        int numResults = 0;
        int numProteins = 0;
        while (provider.hasNextSearchScan()) {
            ProlucidSearchScan scan = null;
            try {
                scan = provider.getNextSearchScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            int scanId = getScanId(runId, scan.getScanNumber());
            // save spectrum data
            uploadSearchScan(scan, lastUploadedRunSearchId, scanId); 

            // save all the search results for this scan
            for (ProlucidSearchResult result: scan.getScanResults()) {
                uploadSearchResult(result, searchId, scanId, searchDbName);
                numResults++;
                numProteins += result.getProteinMatchList().size();
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results, "+numProteins+" protein matches. (runSearchId: "+lastUploadedRunSearchId+")");
                
    }

    
    private ProlucidSearch makeSearchObject(final ProlucidParamsParser parser, final String remoteServer, final String remoteDirectory, final Date searchDate) {
        return new ProlucidSearch() {
            @Override
            public List<ProlucidParam> getProlucidParams() {return parser.getParamList();}
            @Override
            public List<MsResidueModification> getDynamicResidueMods() {return parser.getDynamicResidueMods();}
            @Override
            public List<MsTerminalModification> getDynamicTerminalMods() {return parser.getDynamicTerminalMods();}
            @Override
            public List<MsEnzyme> getEnzymeList() {
                if (parser.isEnzymeUsedForSearch())
                    return Arrays.asList(new MsEnzyme[]{parser.getSearchEnzyme()});
                else 
                    return new ArrayList<MsEnzyme>(0);
            }
            @Override
            public List<MsSearchDatabase> getSearchDatabases() {return Arrays.asList(new MsSearchDatabase[]{parser.getSearchDatabase()});}
            @Override
            public List<MsResidueModification> getStaticResidueMods() {return parser.getStaticResidueMods();}
            @Override
            public List<MsTerminalModification> getStaticTerminalMods() {return parser.getStaticTerminalMods();}
            @Override
            public String getAnalysisProgramName() {return parser.getSearchProgramName();}
            @Override
            public String getAnalysisProgramVersion() {return null;} // we don't have this information in search.xml
            public Date getSearchDate() {return searchDate;}
            public String getServerAddress() {return remoteServer;}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    private int uploadSearchResult(ProlucidSearchResult result, int searchId, int scanId, String searchDbName) throws UploadException {
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, searchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, resultId, searchDbName);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, searchId);
        
        // upload dynamic terminal mods for this result
        uploadResultTerminalMods(result, resultId, searchId);
        
        // upload the SQT file specific information for this result.
        uploadProlucidResultData(result.getProlucidResultData(), resultId);
        
        return resultId;
    }

    private void uploadProlucidResultData(ProlucidResultData resultData, int resultId) {
        // upload the Prolucid specific result information if the cache has enough entries
        if (prolucidResultDataList.size() >= BUF_SIZE) {
            uploadProlucidResultBuffer();
        }
        // add the Prolucid specific information for this result to the cache
        ResultData resultDataDb = new ResultData(resultId, resultData);
        prolucidResultDataList.add(resultDataDb);
    }
    
    private void uploadProlucidResultBuffer() {
        ProlucidSearchResultDAO sqtResultDao = daoFactory.getProlucidResultDAO();
        sqtResultDao.saveAllProlucidResultData(prolucidResultDataList);
        prolucidResultDataList.clear();
    }
    
    protected void flush() {
        super.flush();
        if (prolucidResultDataList.size() > 0) {
            uploadProlucidResultBuffer();
        }
    }
    
    static final class ResultData implements ProlucidResultDataDb {
        
        private final ProlucidResultData data;
        private final int resultId;
        public ResultData(int resultId, ProlucidResultData data) {
            this.data = data;
            this.resultId = resultId;
        }
        @Override
        public int getResultId() {
            return resultId;
        }
        @Override
        public BigDecimal getCalculatedMass() {
            return data.getCalculatedMass();
        }
        @Override
        public BigDecimal getDeltaCN() {
            return data.getDeltaCN();
        }
        @Override
        public int getMatchingIons() {
            return data.getMatchingIons();
        }
        @Override
        public int getPredictedIons() {
            return data.getPredictedIons();
        }
        @Override
        public BigDecimal getSp() {
            return data.getSp();
        }
        @Override
        public int getSpRank() {
            return data.getSpRank();
        }
        @Override
        public BigDecimal getxCorr() {
            return data.getxCorr();
        }
        @Override
        public int getxCorrRank() {
            return data.getxCorrRank();
        }
        @Override
        public Double getBinomialScore() {
            return data.getBinomialScore();
        }
        @Override
        public Double getZscore() {
            return data.getZscore();
        }
    }
}
