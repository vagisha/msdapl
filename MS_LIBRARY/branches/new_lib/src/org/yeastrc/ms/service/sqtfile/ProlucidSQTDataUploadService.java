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

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.Score;
import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public final class ProlucidSQTDataUploadService extends AbstractSQTDataUploadService {

private static final String PROLUCID_PARAMS_FILE = "search.xml";
    
    List<ProlucidResultDataDb> prolucidResultDataList; // prolucid scores
    
    private MsSearchDatabase db = null;
    private List<MsResidueModification> dynaResidueMods;
    private List<MsTerminalModification> dynaTermMods;
    private Score spColScore;
    private Score xcorrColScore;
    private Score deltaCnColScore;
    
    
    public ProlucidSQTDataUploadService() {
        super();
        this.prolucidResultDataList = new ArrayList<ProlucidResultDataDb>();
        this.dynaResidueMods = new ArrayList<MsResidueModification>();
        this.dynaTermMods = new ArrayList<MsTerminalModification>();
    }
    
    void reset() {
        super.reset();
        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
        spColScore = null;
        xcorrColScore = null;
        deltaCnColScore = null;
    }
    
    // resetCaches() is called by reset() in the superclass.
    void resetCaches() {
        super.resetCaches();
        prolucidResultDataList.clear();
    }
    
    MsSearchDatabase getSearchDatabase() {
        return db;
    }

    SearchProgram getSearchProgram() {
        return SearchProgram.PROLUCID;
    }
    
    @Override
    int uploadSearchParameters(String paramFileDirectory, String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        // parse the parameter file 
        ProlucidParamsParser parser = parseProlucidParams(paramFileDirectory, remoteServer);
        
        db = parser.getSearchDatabase();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        // what is the score type in the sp column
        spColScore = parser.getSpColumnScore();
        // what is the score type in the xcorr column
        xcorrColScore = parser.getXcorrColumnScore();
        // what is the score type in the deltaCN column
        deltaCnColScore = parser.getDeltaCNColumnScore();
        
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MsSearchDAO<ProlucidSearch, ProlucidSearchDb> searchDAO = DAOFactory.instance().getProlucidSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, remoteServer, remoteDirectory, searchDate));
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private ProlucidParamsParser parseProlucidParams(String fileDirectory, final String remoteServer) throws UploadException {
        
        log.info("BEGIN ProLuCID search upload -- parsing search.xml");
        String paramFile = fileDirectory+File.separator+PROLUCID_PARAMS_FILE;
        if (!(new File(paramFile).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_PROLUCID_PARAMS);
            throw ex;
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
            throw ex;
        }
    }


    @Override
    void uploadSqtFile(String filePath, int runId) throws UploadException {
        
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        lastUploadedRunSearchId = 0;
        long startTime = System.currentTimeMillis();
        ProlucidSQTFileReader provider = new ProlucidSQTFileReader();
        
        try {
            provider.open(filePath, spColScore, xcorrColScore, deltaCnColScore);
            provider.setDynamicResidueMods(dynaResidueMods);
            provider.setDynamicTerminalMods(dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        try {
            uploadProlucidSqtFile(provider, searchId, runId, searchDatabaseId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            String msg = ex.getErrorMessage() == null ? "" : ex.getErrorMessage();
            ex.setErrorMessage(msg+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        
    }
    
    
    // parse and upload a sqt file
    private void uploadProlucidSqtFile(ProlucidSQTFileReader provider, int searchId, int runId, int searchDbId) throws UploadException {
        
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
                uploadSearchResult(result, lastUploadedRunSearchId, scanId);
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
            public SearchProgram getSearchProgram() {return parser.getSearchProgram();}
            @Override
            public String getSearchProgramVersion() {return null;} // we don't have this information in search.xml
            public Date getSearchDate() {return searchDate;}
            public String getServerAddress() {return remoteServer;}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void uploadSearchResult(ProlucidSearchResult result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = super.uploadBaseSearchResult(result, runSearchId, scanId);
        
        // upload the SQT prolucid specific information for this result.
        uploadProlucidResultData(result.getProlucidResultData(), resultId);
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
