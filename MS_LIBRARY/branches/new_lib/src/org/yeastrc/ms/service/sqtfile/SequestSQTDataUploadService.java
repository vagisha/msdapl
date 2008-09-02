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
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
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
    
    
    public SequestSQTDataUploadService() {
        super();
        this.sequestResultDataList = new ArrayList<SequestResultDataDb>();
    }

    void resetCaches() {
        super.resetCaches();
        sequestResultDataList.clear();
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
    public int uploadSequestSearch(String fileDirectory, Set<String> fileNames, Map<String,Integer> runIdMap, String remoteServer, String remoteDirectory, Date searchDate) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numSearchesToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        // parse the parameter file 
        SequestParamsParser parser = parseSequestParams(fileDirectory, remoteServer);
        if (parser == null)
            return 0;
        
        // dynamic residue mods found in the params file
        List<MsResidueModification> dynaResMods = parser.getDynamicResidueMods();
        // do the parameters indicate that e-value will be reported in the resuting sqt files? 
        boolean usesEvalue = parser.reportEvalue();
        
        // database used for the search (will be used to look up protein ids later)
        String searchDbName = new File(parser.getSearchDatabase().getServerPath()).getName();
        int searchDbId = 0; 
        try {searchDbId = NrSeqLookupUtil.getDatabaseId(searchDbName);}
        catch(NrSeqLookupException e) {
            UploadException ex = new UploadException(ERROR_CODE.SEARCHDB_NOT_FOUND, e);
            ex.setErrorMessage(e.getMessage());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return 0;
        }
        
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
                log.error("No runId for sqt file: "+file);
                continue;
            }
            // Consume any exceptions during parsing and upload of a sqt file. If exceptions occur, this search will be deleted
            // but the rest of the upload will continue.
            uploadSequestSqtFile(remoteServer, filePath, searchId, runId, dynaResMods, usesEvalue, searchDbId);
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
    
    private SequestParamsParser parseSequestParams(String fileDirectory, final String remoteServer) {
        
        log.info("BEGIN Sequest search UPLOAD -- parsing sequest.params");
        String paramFile = fileDirectory+File.separator+SEQUEST_PARAMS_FILE;
        if (!(new File(paramFile).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SEQUEST_PARAMS);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return null;
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
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tSEARCH WILL NOT BE UPLOADED", ex);
            return null;
        }
    }
    
    // parse and upload data from the sequest.params file
    private int uploadSearchParams(SequestParamsParser parser, final String remoteServer, 
            final String remoteDirectory, final Date searchDate) {
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MsSearchDAO<SequestSearch, SequestSearchDb> searchDAO = DAOFactory.instance().getSequestSearchDAO();
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
    private void uploadSequestSqtFile(final String remoteServer, String filePath, 
            int searchId, int runId, List<MsResidueModification> dynaResMods, 
            boolean usesEvalue, int searchDbId) {
        
        resetCaches();
        
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        lastUploadedRunSearchId = 0;
        long startTime = System.currentTimeMillis();
        SequestSQTFileReader provider = new SequestSQTFileReader();
        
        try {
            provider.open(filePath, remoteServer, usesEvalue);
            provider.setDynamicResidueMods(dynaResMods);
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
            uploadSequestSqtFile(provider, searchId, runId, searchDbId);
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
        int numProteins = 0;
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
                uploadSearchResult(result, lastUploadedRunSearchId, scanId, searchDbId);
                numResults++;
                numProteins += result.getProteinMatchList().size();
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results, "+numProteins+" protein matches. (runSearchId: "+lastUploadedRunSearchId+")");
                
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
            public String getAnalysisProgramName() {return parser.getSearchProgramName();}
            @Override
            public String getAnalysisProgramVersion() {return null;} // we don't have this information in sequest.params
            public Date getSearchDate() {return searchDate;}
            public String getServerAddress() {return remoteServer;}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    private int uploadSearchResult(SequestSearchResult result, int runSearchId, int scanId, int searchDbId) throws UploadException {
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, resultId, searchDbId);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);
        
        // no dynamic terminal mods for sequest
        
        // upload the SQT file specific information for this result.
        uploadSequestResultData(result.getSequestResultData(), resultId);
        
        return resultId;
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
