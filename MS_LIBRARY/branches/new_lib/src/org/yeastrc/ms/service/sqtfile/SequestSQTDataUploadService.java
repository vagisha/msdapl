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

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.dao.util.NrSeqLookupUtil;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ResultResidueModification;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class SequestSQTDataUploadService {

    private static final Logger log = Logger.getLogger(SequestSQTDataUploadService.class);
    
    private static final DAOFactory daoFactory = DAOFactory.instance();
    
    private static final DynamicModLookupUtil dynaModLookup = DynamicModLookupUtil.instance();
    
    public static final int BUF_SIZE = 1000;
    
    private static final String SEQUEST_PARAMS_FILE = "sequest.params";
    
    // these are the things we will cache and do bulk-inserts
    List<MsSearchResultProteinDb> proteinMatchList; // protein matches
    List<SequestResultDataDb> sequestResultDataList; // sequest scores
    List<MsResultDynamicResidueModDb> resultModList; // dynamic residue modifications
    
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private int searchId;
    private int lastUploadedRunSearchId;
    
    private boolean usesEvalue = false;
    private String searchDbName;
    
    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msSearch and msSequenceDatabaseDetail table.
//    private long dbSequenceLength;
//    private int dbProteinCount;
    private String sequestVersion = "uninit";
   
    
    private void init() {
        
        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup.reset();
        
        searchId = 0;
        lastUploadedRunSearchId = 0;
        usesEvalue = false;
        searchDbName = null;
        
        numSearchesToUpload = 0;
        numSearchesUploaded = 0;
        
//        dbSequenceLength = 0;
//        dbProteinCount = 0;
        sequestVersion = "uninit";
        
        if (proteinMatchList != null)
            proteinMatchList.clear();
        else
            proteinMatchList = new ArrayList<MsSearchResultProteinDb>();
        if (sequestResultDataList != null)
            sequestResultDataList.clear();
        else
            sequestResultDataList = new ArrayList<SequestResultDataDb>();
        if (resultModList != null)
            resultModList.clear();
        else
            resultModList = new ArrayList<MsResultDynamicResidueModDb>();
        if (uploadExceptionList != null)
            uploadExceptionList.clear();
        else
            uploadExceptionList = new ArrayList<UploadException>();
    }
    
    public int getUploadedSearchId() {
        return searchId;
    }
    
    public List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }
    
    public int getNumSearchesToUpload() {
        return numSearchesToUpload;
    }
    
    public int getNumSearchesUploaded() {
        return numSearchesUploaded;
    }
    
    /**
     * @param fileDirectory 
     * @param fileNames names of sqt files (without the .sqt extension)
     * @param runIdMap map mapping file names to runIds in database
     * @param remoteServer 
     * @param remoteDirectory 
     * @param searchDate 
     * @return
     * @throws UploadException 
     */
    public void uploadSQTSearch(String fileDirectory, Set<String> fileNames, Map<String,Integer> runIdMap, String remoteServer, String remoteDirectory, Date searchDate) {

        init();// reset all caches etc.
        
        // parse the parameter file and upload to-level search data
        searchId = uploadSearch(fileDirectory, remoteServer, remoteDirectory, searchDate);
        // if an error happened while parsing or uploading the parameters file don't go any further.
        if (searchId == 0)
            return;
        
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
            // but the experiment upload will continue.
            uploadSequestSqtFile(remoteServer, filePath, searchId, runId);
        }
        

        // Update the "sequenceLength" and "proteinCount" columns in the msSequenceDataDetail table
        // TODO
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (sequestVersion != null && !("uninit".equals(sequestVersion))) {
            updateSequestVersion(searchId, sequestVersion);
        }
    }
    
    private void updateSequestVersion(int searchId, String sequestVersion) {
        try {
            MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchAnalysisProgramVersion(searchId, sequestVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating sequest version for searchID: "+searchId, e);
        }
    }
    
    // parse and upload data from the sequest.params file
    private int uploadSearch(String fileDirectory, final String remoteServer, final String remoteDirectory, final Date searchDate) {
        
        log.info("BEGIN Sequest search UPLOAD");
        String paramFile = fileDirectory+File.separator+SEQUEST_PARAMS_FILE;
        if (!(new File(paramFile).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SEQUEST_PARAMS);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return 0;
        }
        // parse the parameters file
        final SequestParamsParser parser = new SequestParamsParser(remoteServer);
        try {
            parser.parseParamsFile(paramFile);
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(paramFile);
            ex.setErrorMessage(e.getMessage());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return 0;
        }
        
        // do the parameters indicate that e-value will be reported in the resuting sqt files? 
        this.usesEvalue = parser.reportEvalue();
        // database used for the search (will be used to look up protein ids later)
        this.searchDbName = parser.getSearchDatabase().getServerPath();
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MsSearchDAO<SequestSearch, SequestSearchDb> searchDAO = DAOFactory.instance().getSequestSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, remoteServer, remoteDirectory, searchDate));
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR);
            ex.setFile(paramFile);
            ex.setErrorMessage(e.getMessage());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return 0;
        }
    }

    // Consume any exceptions during parsing and upload of a sqt file. If exceptions occur, this search will be deleted
    // but the experiment upload will continue.
    private void uploadSequestSqtFile(final String remoteServer, String filePath, int searchId, int runId) {
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        numSearchesToUpload++;
        lastUploadedRunSearchId = 0;
        long startTime = System.currentTimeMillis();
        SequestSQTFileReader provider = new SequestSQTFileReader(remoteServer, usesEvalue);
        try {
            provider.open(filePath);
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        finally {provider.close();}
        
        try {
            uploadSequestSqtFile(provider, searchId, runId);
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
    private void uploadSequestSqtFile(SequestSQTFileReader provider, int searchId, int runId) throws UploadException {
        
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
                uploadSearchResult(result, searchId, scanId);
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
            public List<MsEnzyme> getEnzymeList() {return Arrays.asList(new MsEnzyme[]{parser.getSearchEnzyme()});}
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
    
    private int uploadSearchHeader(SQTSearchDataProvider provider, int runId, int searchId) throws DataProviderException {
        
        SQTRunSearch search = provider.getSearchHeader();
        if (search instanceof SQTHeader) {
            SQTHeader header = (SQTHeader)search;
            // this is the first time we are assigning a value to sequestVersion
            if ("uninit".equals(sequestVersion))
                this.sequestVersion = header.getSearchEngineVersion();
            
            // make sure the sequestVersion value is same in all sqt header
            // if not we set sequestVersion to null so that the analysisProgramVersion field does
            // not get updated. 
            if (sequestVersion != null &&
                    !sequestVersion.equals(header.getSearchEngineVersion())) {
                this.sequestVersion = null;
            }
        }
        // save the run search and return the database id
        MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> runSearchDao = daoFactory.getSqtRunSerachDAO();
        return runSearchDao.saveRunSearch(search, runId, searchId);
    }
    
    private void uploadSearchScan(SQTSearchScan scan, int runSearchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, runSearchId, scanId);
    }
    
    private int uploadSearchResult(SequestSearchResult result, int searchId, int scanId) throws UploadException {
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, searchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, resultId);
        
        // upload dynamic mods for this result
        uploadResultMods(result, resultId, searchId);
        
        // upload the SQT file specific information for this result.
        uploadSequestResultData(result.getSequestResultData(), resultId);
        
        return resultId;
    }

    private void uploadProteinMatches(SequestSearchResult result, final int resultId) throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            final int proteinId = NrSeqLookupUtil.getProteinId(searchDbName, match.getAccession());
            if (proteinId == 0) {
               UploadException ex = new UploadException(ERROR_CODE.PROTEIN_NOT_FOUND);
               ex.setErrorMessage("No match found for protein: "+match.getAccession()+" in database: "+searchDbName);
               throw ex;
            }
            proteinMatchList.add(new MsSearchResultProteinDb(){
                public int getId() { throw new UnsupportedOperationException("getId() not supported by anonymous class");}
                public int getProteinId() { return proteinId; }
                public int getResultId() { return resultId; }
                });
        }
    }

    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        matchDao.saveAll(proteinMatchList);
        proteinMatchList.clear();
    }
    
    private void uploadResultMods(SequestSearchResult result, int resultId, int searchId) {
        // upload the result dynamic modifications if the cache has enough entries
        if (resultModList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the dynamic modifications for this result to the cache
        for (MsResultDynamicResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicResidueModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass());
            resultModList.add(new ResultResidueMods(mod.getModifiedResidue(), 
                                                    mod.getModificationSymbol(),
                                                    mod.getModificationMass(), 
                                                    mod.getModifiedPosition(),
                                                    resultId,
                                                    modId));
        }
    }

    private void uploadResultModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicResidueModsForResult(resultModList);
        resultModList.clear();
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
    
    private void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (sequestResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
        if (resultModList.size() > 0) {
            uploadResultModBuffer();
        }
    }
    
    private static int getScanId(int runId, int scanNumber) throws UploadException {
        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    
    public static void deleteSearch(int searchId) {
        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        searchDao.deleteSearch(searchId);
    }
   
    private void deleteLastUploadedRunSearch() {
        if (lastUploadedRunSearchId == 0)
            return;
        MsRunSearchDAO<MsRunSearch, MsRunSearchDb> runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        runSearchDao.deleteRunSearch(lastUploadedRunSearchId);
    }
    
    
    private static final class ResultResidueMods extends ResultResidueModification implements MsResultDynamicResidueModDb {

        private final int resultId;
        private final int modId;
        public ResultResidueMods(char modResidue, char modSymbol,
                BigDecimal modMass, int position, int resultId, int modId) {
            super(modResidue, modSymbol, modMass, position);
            this.resultId = resultId;
            this.modId = modId;
        }
        public int getModificationId() {
            return modId;
        }
        public int getResultId() {
            return resultId;
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
