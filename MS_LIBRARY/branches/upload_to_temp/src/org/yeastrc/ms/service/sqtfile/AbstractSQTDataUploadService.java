package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTRunSearchWrap;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.SearchDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public abstract class AbstractSQTDataUploadService implements SearchDataUploadService {

    static final Logger log = Logger.getLogger(AbstractSQTDataUploadService.class);

    private final MsRunDAO runDao;
    private final MsScanDAO scanDao;
    private final MsSearchDatabaseDAO sequenceDbDao;
    private final SQTRunSearchDAO runSearchDao;
    private final MS2ScanChargeDAO ms2ScanChargeDao;
    private final MsSearchResultProteinDAO proteinMatchDao;
    private final MsSearchModificationDAO modDao;
    private final MsSearchResultDAO resultDao;
    private final MsSearchDAO searchDao;
    private final SQTSearchScanDAO spectrumDataDao;

    private DynamicModLookupUtil dynaModLookup;

    static final int BUF_SIZE = 500;
    static final int RESULT_BUF_SIZE = BUF_SIZE;
    
    // these are the things we will cache and do bulk-inserts
    List<MsSearchResultProtein> proteinMatchList;
    List<MsResultResidueModIds> resultResidueModList;
    List<MsResultTerminalModIds> resultTerminalModList;
    Map<String, SQTSearchScan> searchScanMap;


    private int numSearchesUploaded = 0;
    
    boolean useXcorrRankCutoff = false;
    int xcorrRankCutoff = Integer.MAX_VALUE;
    
    // This is information we will get from the SQT files and then update the entries in the msSearch and msSequenceDatabaseDetail table.
    private String programVersion = "uninit";

    private int experimentId;
    private java.util.Date searchDate;
    private String dataDirectory;
    private String decoyDirectory;
    private String remoteServer;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    private boolean preUploadCheckDone = false;
    
    private List<String> filenames;
    private List<String> spectrumFileNames;
    
    int searchId;
    int sequenceDatabaseId; // nrseq database id

    
    boolean doScanChargeMassCheck = false; // for data from MacCoss lab

    
    public AbstractSQTDataUploadService() {
        
        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);
        this.searchScanMap = new HashMap<String, SQTSearchScan>((int) (BUF_SIZE*1.5));
        
        preUploadCheckMsg = new StringBuilder();
        filenames = new ArrayList<String>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        
        this.runDao = daoFactory.getMsRunDAO(); 
        this.scanDao = daoFactory.getMsScanDAO();
        this.ms2ScanChargeDao = daoFactory.getMS2FileScanChargeDAO();
        this.sequenceDbDao = daoFactory.getMsSequenceDatabaseDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getSqtRunSearchDAO();
        
        this.proteinMatchDao = daoFactory.getMsProteinMatchDAO();
        this.modDao = daoFactory.getMsSearchModDAO();
        this.resultDao = daoFactory.getMsSearchResultDAO();
        this.spectrumDataDao = daoFactory.getSqtSpectrumDAO();
        
    }
    
    public void setXcorrRankCutoff(int cutoff) {
        if(cutoff < Integer.MAX_VALUE && cutoff > 0) {
            xcorrRankCutoff = cutoff;
            useXcorrRankCutoff = true;
        }
    }
    
    protected String getDecoyDirectory() {
        return this.decoyDirectory;
    }
    protected String getDataDirectory() {
        return this.dataDirectory;
    }
    public List<String> getFileNames() {
        return this.filenames;
    }
    
    void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesUploaded = 0;

        resetCaches();

        searchId = 0;
        sequenceDatabaseId = 0;
        programVersion = "uninit";
        
        preUploadCheckMsg = new StringBuilder();
    }

    // called before uploading each sqt file and in the reset() method.
    void resetCaches() {
        
        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
        searchScanMap.clear();

//        lastUploadedRunSearchId = 0;
    }

//    public final List<UploadException> getUploadExceptionList() {
//        return this.uploadExceptionList;
//    }

    public final int getNumSearchesUploaded() {
        return numSearchesUploaded;
    }

    int getScanId(int runId, int scanNumber)
            throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }

    void updateProgramVersion(int searchId, String programVersion) {
        try {
            searchDao.updateSearchProgramVersion(searchId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for searchID: "+searchId, e);
        }
    }

//    static void updateProgram(int searchId, Program program) {
//        try {
//            MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
//            searchDao.updateSearchProgram(searchId, program);
//        }
//        catch(RuntimeException e) {
//            log.warn("Error updating search program for searchID: "+searchId, e);
//        }
//    }
    
    //--------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    
    abstract String searchParamsFile();
    
    abstract int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory, java.util.Date searchDate) throws UploadException;
    
    // NOTE: this method should be called AFTER uploadSearchParameters.
    abstract MsSearchDatabaseIn getSearchDatabase();
    
    public abstract Program getSearchProgram();
    
    abstract SearchFileFormat getSearchFileFormat();
    
    abstract int uploadSqtFile(String filePath, int runId) throws UploadException;
    
    //--------------------------------------------------------------------------------------------------
    @Override
    public void upload() throws UploadException {

        reset();// reset all caches etc.
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        // get the runIds corresponding to the files we will be uploading
        Map<String, Integer> runIdMap;
        try {
           runIdMap = createRunIdMap();
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        
        // parse and upload the search parameters
        try {
            searchId = uploadSearchParameters(experimentId, dataDirectory, 
                    remoteServer, remoteDirectory, searchDate);
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        // initialize the Modification lookup map; will be used when uploading modifications for search results
        dynaModLookup = new DynamicModLookupUtil(searchId);
        
        
        // now upload the individual sqt files
        for (String file: filenames) {
            String filePath = dataDirectory+File.separator+file;
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            Integer runId = runIdMap.get(file); 
            
            resetCaches();
            int runSearchId;
            try {
                runSearchId = uploadSqtFile(filePath, runId);
                
                numSearchesUploaded++;
            }
            catch (UploadException ex) {
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                throw ex;
            }
        }
        
        // if no sqt files were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
            deleteSearch(searchId);
            numSearchesUploaded = 0;
            throw ex;
        }
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (this.programVersion != null && this.programVersion != "uninit") {
            updateProgramVersion(searchId, programVersion);
        }
        
        // copy files to a directory
        copyFiles(experimentId);
        
    }
    
    public int getUploadedSearchId() {
        return searchId;
    }

    protected abstract void copyFiles(int experimentId) throws UploadException;
    

    private Map<String, Integer> createRunIdMap() throws UploadException {
        
        Map<String, Integer> runIdMap = new HashMap<String, Integer>(filenames.size()*2);
        for(String file: filenames) {
            String filenoext = removeFileExtension(file);
            int runId = 0;
            try {runId = runDao.loadRunIdForExperimentAndFileName(experimentId, filenoext);}
            catch(Exception e) {
                UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR);
                throw ex;
            }
            if(runId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SQT);
                ex.appendErrorMessage("File: "+filenoext);
                throw ex;
            }
            runIdMap.put(file, runId);
        }
        return runIdMap;
    }
    
    // get the id of the search database used (will be used to look up protein ids later)
    int getSearchDatabaseId(MsSearchDatabaseIn db) throws UploadException {
        String searchDbName = null;
        int dbId = 0;
        if (db != null) {
            
            // look in the msSequenceDatabaseDetail table first. We might already have this 
            // database in there
            dbId = sequenceDbDao.getSequenceDatabaseId(db.getServerPath());
            if(dbId == 0) {
                searchDbName = db.getDatabaseFileName();
                dbId = NrSeqLookupUtil.getDatabaseId(searchDbName);
            }
        }
        if (dbId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.SEARCHDB_NOT_FOUND);
            ex.setErrorMessage("No database ID found for: "+searchDbName);
            throw ex;
        }
        return dbId;
    }
    
    // RUN SEARCH
    final int uploadSearchHeader(SQTSearchDataProvider<?> provider, int runId, int searchId)
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
                    throw new DataProviderException("Value of SQTGeneratorVersion is not nulllthe same in all SQT files.");
            }
            else if (!programVersion.equals(header.getSearchEngineVersion())) {
                throw new DataProviderException("Value of SQTGeneratorVersion is not the same in all SQT files.");
            }
        }
        // save the run search and return the database id
//        SQTRunSearchDAO runSearchDao = daoFactory.getSqtRunSearchDAO();
        return runSearchDao.saveRunSearch(new SQTRunSearchWrap(search, searchId, runId));
    }

    private SQTSearchScan getOldScanIfExists(int runSearchId, int scanId, int charge, BigDecimal observedMass) {
        // look in the cache first
        SQTSearchScan scan = searchScanMap.get(scanId+"_"+charge+"_"+observedMass);
        if(scan != null)   return scan;
        // now look in the database
        return spectrumDataDao.load(runSearchId, scanId, charge, observedMass);
    }
    
    /**
     * @param scan
     * @param runSearchId
     * @param scanId
     * @return true if the search scan got uploaded, false otherwise
     * @throws UploadException 
     */
    final boolean uploadSearchScan(SQTSearchScanIn<?> scan, int runSearchId, int scanId) throws UploadException {
        
        // NOTE: Added some changes to deal with duplicate results in MacCoss lab data
        // This will not work if the raw data was not MS2 or CMS2
        int charge = scan.getCharge();
        if(doScanChargeMassCheck) {
            // get the Z lines for the MS2 files 
            List<MS2ScanCharge> scanChgStates = ms2ScanChargeDao.loadScanChargesForScan(scanId);
            if(scanChgStates.size() == 0) {
                UploadException ex = new UploadException(ERROR_CODE.SCAN_CHARGE_NOT_FOUND);
                ex.setErrorMessage("No matching scan+charge results found for: scanId: "+
                        scanId);
                throw ex;
            }
            // if we don't find a match with the charge and observed mass of the given scan
            // we will not upload this scan
            boolean found = false;
            for(MS2ScanCharge sc: scanChgStates) {
                if(sc.getCharge() == charge && //sc.getMass().doubleValue() == scan.getObservedMass().doubleValue()) {
                         Math.abs(sc.getMass().doubleValue() - scan.getObservedMass().doubleValue()) <= 0.0001){
                    found = true;
                    break;
                }
            }
            if(!found) {
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage("No matching scan+charge result found for: scanId: "+
                      scanId+"; scanNumber: "+scan.getScanNumber()+"; charge: "+charge+"; mass: "+scan.getObservedMass());
                throw ex;
//                log.info("No matching scan+charge result found for: scanId: "+
//                        scanId+"; charge: "+charge+"; mass: "+scan.getObservedMass());
//                return false;
            }
            
            // sometimes results can be exact duplicates.  In this case we will keep the old result and ignore this one
            if(found) {
                SQTSearchScan oldScan = getOldScanIfExists(runSearchId, scanId, charge, scan.getObservedMass());
                if(oldScan != null) {
                    log.info("Duplicate scan+charge result found for: scanId: "+
                            scanId+"; charge: "+charge+"; mass: "+scan.getObservedMass());
                    return false;
                }
            }
        }
        // save the scan+charge data
        uploadSearchScan(new SQTSearchScanWrap(scan, runSearchId, scanId));
        return true;
    }

    
    final int uploadBaseSearchResult(MsSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, resultId, sequenceDatabaseId);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);
        
        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);
        
        return resultId;
    }
    
    final <T extends MsSearchResult> List<Integer> uploadBaseSearchResults(List<T> results) throws UploadException {
        
        
        List<Integer> autoIncrIds = resultDao.saveResultsOnly(results);
        for(int i = 0; i < results.size(); i++) {
            MsSearchResult result = results.get(i);
            int resultId = autoIncrIds.get(i);
            
            // upload the protein matches
            uploadProteinMatches(result, resultId, sequenceDatabaseId);
            
            // upload dynamic mods for this result
            uploadResultResidueMods(result, resultId, result.getRunSearchId());
            
            // no dynamic terminal mods for sequest
            uploadResultTerminalMods(result, resultId, searchId);
        }
        
        return autoIncrIds;
    }


    // SEARCH SCAN
    final void uploadSearchScan(SQTSearchScan searchScan) throws UploadException {
        // if the cache has enough entries upload 
        if(searchScanMap.size() >= BUF_SIZE) {
            uploadSearchScanBuffer();
        }
        String key = searchScan.getScanId()+"_"+searchScan.getCharge()+"_"+searchScan.getObservedMass();
        if(searchScanMap.get(key) != null) {
            UploadException ex = new UploadException(ERROR_CODE.DUPLICATE_SCAN_CHARGE);
            ex.appendErrorMessage("Result already exists for scanID: "+searchScan.getScanId()+
                    " and charge: "+searchScan.getCharge()+" and observedMass: "+searchScan.getObservedMass());
            throw ex;
        }
        searchScanMap.put(key, searchScan);
    }
    
    private void uploadSearchScanBuffer() {
        spectrumDataDao.saveAll(new ArrayList<SQTSearchScan>(this.searchScanMap.values()));
        searchScanMap.clear();
    }
    
    // PROTEIN MATCHES
    final void uploadProteinMatches(MsSearchResultIn result, final int resultId, int databaseId)
        throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        Set<String> accSet = new HashSet<String>(result.getProteinMatchList().size());
        for (MsSearchResultProteinIn match: result.getProteinMatchList()) {
            // only UNIQUE accession strings for this result will be added.
            if (accSet.contains(match.getAccession()))
                continue;
            accSet.add(match.getAccession());
            proteinMatchList.add(new SearchResultProteinBean(resultId, match.getAccession()));
        }
    }
    
    final void uploadProteinMatches(MsSearchResult result, final int resultId, int databaseId)
    throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        Set<String> accSet = new HashSet<String>(result.getProteinMatchList().size());
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            // only UNIQUE accession strings for this result will be added.
            if (accSet.contains(match.getAccession()))
                continue;
            accSet.add(match.getAccession());
            proteinMatchList.add(new SearchResultProteinBean(resultId, match.getAccession()));
        }
    }
    
    private void uploadProteinMatchBuffer() {
        
        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        proteinMatchDao.saveAll(list);
        proteinMatchList.clear();
    }

    // RESIDUE DYNAMIC MODIFICATION
    void uploadResultResidueMods(MsSearchResultIn result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicResidueModificationId( 
                    mod.getModifiedResidue(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
                        searchId+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modId, mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }
    
    void uploadResultResidueMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicResidueModificationId( 
                    mod.getModifiedResidue(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
                        searchId+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modId, mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }

    private void uploadResultResidueModBuffer() {
        modDao.saveAllDynamicResidueModsForResult(resultResidueModList);
        resultResidueModList.clear();
    }
    
    // TERMINAL DYNAMIC MODIFICATION
    void uploadResultTerminalMods(MsSearchResultIn result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic terminal modifications if the cache has enough entries
        if (resultTerminalModList.size() >= BUF_SIZE) {
            uploadResultTerminalModBuffer();
        }
        // add the dynamic terminal modifications for this result to the cache
        for (MsTerminalModificationIn mod: result.getResultPeptide().getResultDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicTerminalModificationId( 
                    mod.getModifiedTerminal(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic terminal modification found for: searchId: "+
                        searchId+
                        "; modTerminal: "+mod.getModifiedTerminal()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultTerminalModIds resultMod = new ResultTerminalModIds(resultId, modId);
            resultTerminalModList.add(resultMod);
        }
    }
    
    void uploadResultTerminalMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic terminal modifications if the cache has enough entries
        if (resultTerminalModList.size() >= BUF_SIZE) {
            uploadResultTerminalModBuffer();
        }
        // add the dynamic terminal modifications for this result to the cache
        for (MsTerminalModificationIn mod: result.getResultPeptide().getResultDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicTerminalModificationId( 
                    mod.getModifiedTerminal(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic terminal modification found for: searchId: "+
                        searchId+
                        "; modTerminal: "+mod.getModifiedTerminal()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultTerminalModIds resultMod = new ResultTerminalModIds(resultId, modId);
            resultTerminalModList.add(resultMod);
        }
    }

    private void uploadResultTerminalModBuffer() {
        modDao.saveAllDynamicTerminalModsForResult(resultTerminalModList);
        resultTerminalModList.clear();
    }

    void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (resultResidueModList.size() > 0) {
            uploadResultResidueModBuffer();
        }
        if (resultTerminalModList.size() > 0) {
            uploadResultTerminalModBuffer();
        }
        if(searchScanMap.size() > 0) {
            uploadSearchScanBuffer();
        }
    }

    public void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        log.info("Deleting search ID: "+searchId);
        searchDao.deleteSearch(searchId);
    }
    
    
    @Override
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }
    
    @Override
    public void setSearchDate(java.util.Date date) {
        this.searchDate = date;
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }
    
    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }
    
    @Override
    public void setDecoyDirectory(String directory) {
        this.decoyDirectory = directory;
    }
    
    @Override
    public void setRemoteDecoyDirectory(String directory) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void checkResultChargeMass(boolean check) {
        this.doScanChargeMassCheck = check;
    }
    
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
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
        
        // 2. valid and supported search data format
        // 3. consistent data format 
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".sqt");
            }});
        for (int i = 0; i < files.length; i++) {
            filenames.add(files[i].getName());
        }
        
        // make sure all files are of the same type
        for (String file: filenames) {
            String sqtFile = dataDirectory+File.separator+file;
            SearchFileFormat myType = SQTFileReader.getSearchFileType(sqtFile);
            
            if (myType == null) {
                appendToMsg("Cannot determine SQT type for file: "+file);
                return false;
            }
            
            // For now we support only sequest and ProLuCID sqt files. 
            if(myType != getSearchFileFormat()) {
                appendToMsg("Unsupported SQT type for uploader. Expected: "+getSearchFileFormat()+"; Found: "+myType+". File: "+file);
                return false;
            }
        }
        
        // 4. If we know the raw data file names that will be uploaded match them with up with the SQT files
        //    and make sure there is a spectrum data file for each SQT file
        if(spectrumFileNames != null) {
            for(String file:filenames) {
                String filenoext = removeFileExtension(file);
                if(!spectrumFileNames.contains(filenoext)) {
                    appendToMsg("No corresponding spectrum data file found for: "+filenoext);
                    return false;
                }
            }
        }
        
        // 5. Make sure the search parameters file is present
        File paramsFile = new File(dataDirectory+File.separator+searchParamsFile());
        if(!paramsFile.exists()) {
            appendToMsg("Cannot find search parameters file: "+paramsFile.getAbsolutePath());
            return false;
        }
        
        preUploadCheckDone = true;
        
        return true;
    }

    private String removeFileExtension(String file) {
        int idx = file.lastIndexOf(".sqt");
        if(idx == -1)
            idx = file.lastIndexOf(".SQT");
        if(idx != -1)
            return file.substring(0, idx);
        else
            return file;
    }    

    @Override
    public void setSpectrumFileNames(List<String> fileNames) {
        this.spectrumFileNames = fileNames;
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        return "\tSearch file format: "+getSearchFileFormat()+
        "\n\t#Search files in Directory: "+filenames.size()+"; #Uploaded: "+numSearchesUploaded;
    }

}