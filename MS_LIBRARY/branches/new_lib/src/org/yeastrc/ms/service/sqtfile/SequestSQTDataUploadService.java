/**
 * SQTDataUploadService.java
 * @author Vagisha Sharma
 * Jul 15, 2008
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
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public final class SequestSQTDataUploadService extends AbstractSQTDataUploadService {

    
    private static final String SEQUEST_PARAMS_FILE = "sequest.params";
    
    
    // these are the things we will cache and do bulk-inserts
    List<SequestResultDataDb> sequestResultDataList; // sequest scores
    
    private MsSearchDatabase db = null;
    private boolean usesEvalue = false;
    private List<MsResidueModification> dynaResidueMods;
    private List<MsTerminalModification> dynaTermMods;
    
    public SequestSQTDataUploadService() {
        super();
        this.sequestResultDataList = new ArrayList<SequestResultDataDb>();
        this.dynaResidueMods = new ArrayList<MsResidueModification>();
        this.dynaTermMods = new ArrayList<MsTerminalModification>();
    }

    void reset() {
        super.reset();
        usesEvalue = false;
        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
    }
    // resetCaches() is called by reset() in the superclass.
    void resetCaches() {
        super.resetCaches();
        sequestResultDataList.clear();
    }
    
    MsSearchDatabase getSearchDatabase() {
        return db;
    }

    SearchProgram getSearchProgram() {
        return SearchProgram.SEQUEST;
    }
    
    @Override
    int uploadSearchParameters(String paramFileDirectory, String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        SequestParamsParser parser = parseSequestParams(paramFileDirectory, remoteServer);
        
        usesEvalue = parser.reportEvalue();
        db = parser.getSearchDatabase();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MsSearchDAO<SequestSearch, SequestSearchDb> searchDAO = DAOFactory.instance().getSequestSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, remoteServer, remoteDirectory, searchDate));
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private SequestParamsParser parseSequestParams(String fileDirectory, final String remoteServer) throws UploadException {
        
        log.info("BEGIN Sequest search UPLOAD -- parsing sequest.params");
        String paramFile = fileDirectory+File.separator+SEQUEST_PARAMS_FILE;
        if (!(new File(paramFile).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SEQUEST_PARAMS);
            throw ex;
        }
        // parse the parameters file
        final SequestParamsParser parser = new SequestParamsParser();
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
        SequestSQTFileReader provider = new SequestSQTFileReader();
        
        try {
            provider.open(filePath, usesEvalue);
            provider.setDynamicResidueMods(this.dynaResidueMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        try {
            uploadSequestSqtFile(provider, searchId, runId, searchDatabaseId);
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
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
    }
    
    // parse and upload a sqt file
    private void uploadSequestSqtFile(SequestSQTFileReader provider, int searchId, int runId, int searchDbId) throws UploadException {
        
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
        while (provider.hasNextSearchScan()) {
            SequestSearchScan scan = null;
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
            for (SequestSearchResult result: scan.getScanResults()) {
                uploadSearchResult(result, lastUploadedRunSearchId, scanId);
                numResults++;
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results. (runSearchId: "+lastUploadedRunSearchId+")");
                
    }

    
    private SequestSearch makeSearchObject(final SequestParamsParser parser, final String remoteServer, final String remoteDirectory, final Date searchDate) {
        return new SequestSearch() {
            @Override
            public List<SequestParam> getSequestParams() {return parser.getParamList();}
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
            public String getSearchProgramVersion() {return null;} // we don't have this information in sequest.params
            public Date getSearchDate() {return searchDate;}
            public String getServerAddress() {return remoteServer;}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void uploadSearchResult(SequestSearchResult result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = super.uploadBaseSearchResult(result, runSearchId, scanId);
        
        // upload the SQT sequest specific information for this result.
        uploadSequestResultData(result.getSequestResultData(), resultId);
    }

    private void uploadSequestResultData(SequestResultData resultData, int resultId) {
        // upload the Sequest specific result information if the cache has enough entries
        if (sequestResultDataList.size() >= BUF_SIZE) {
            uploadSequestResultBuffer();
        }
        // add the Sequest specific information for this result to the cache
        ResultData resultDataDb = new ResultData(resultId, resultData);
        sequestResultDataList.add(resultDataDb);
    }
    
    private void uploadSequestResultBuffer() {
        SequestSearchResultDAO sqtResultDao = daoFactory.getSequestResultDAO();
        sqtResultDao.saveAllSequestResultData(sequestResultDataList);
        sequestResultDataList.clear();
    }
    
    void flush() {
        super.flush();
        if (sequestResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
    }
    
    private static final class ResultData implements SequestResultDataDb {
        private final SequestResultData data;
        private final int resultId;
        public ResultData(int resultId, SequestResultData data) {
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
        public Double getEvalue() {
            return data.getEvalue();
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
    }
}
