package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.search.MsResultResidueModIn;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.ResultModIdentifier;
import org.yeastrc.ms.domain.search.ResultResidueModIdentifier;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.impl.MsSearchResultProteinDbImpl;
import org.yeastrc.ms.domain.search.impl.ResultModIdentifierImpl;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIdentifierImpl;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public abstract class AbstractSQTDataUploadService {

    static final Logger log = Logger.getLogger(AbstractSQTDataUploadService.class);

    static final DAOFactory daoFactory = DAOFactory.instance();

    private DynamicModLookupUtil dynaModLookup;

    static final int BUF_SIZE = 1000;

    // these are the things we will cache and do bulk-inserts
    LinkedHashSet<MsSearchResultProteinDb> proteinMatchSet;
    List<ResultResidueModIdentifier> resultResidueModList;
    List<ResultModIdentifier> resultTerminalModList;


    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();

    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msSearch and msSequenceDatabaseDetail table.
    private String programVersion = "uninit";
    private SearchProgram programFromSqt = null;

    int lastUploadedRunSearchId;
    int searchId;
    int sequenceDatabaseId; // nrseq database id
    
    public AbstractSQTDataUploadService() {
        this.proteinMatchSet = new LinkedHashSet<MsSearchResultProteinDb>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<ResultResidueModIdentifier>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<ResultModIdentifier>(BUF_SIZE);
        this.uploadExceptionList = new ArrayList<UploadException>();
    }
    
    void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesToUpload = 0;
        numSearchesUploaded = 0;

        resetCaches();

        uploadExceptionList.clear();
        searchId = 0;
        sequenceDatabaseId = 0;
        programVersion = "uninit";
        programFromSqt = null;
    }

    // called before uploading each sqt file and in the reset() method.
    void resetCaches() {
        
        proteinMatchSet.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();

        lastUploadedRunSearchId = 0;
    }

    public final List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }

    public final int getNumSearchesToUpload() {
        return numSearchesToUpload;
    }

    public final int getNumSearchesUploaded() {
        return numSearchesUploaded;
    }

    static int getScanId(int runId, int scanNumber)
            throws UploadException {

        MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SQT_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }

    static int getNumFilesToUpload(String fileDirectory, Set<String> fileNames) {
        int num = 0;
        for (String file: fileNames) {
            if ((new File(fileDirectory+File.separator+file+".sqt")).exists())
                num++;
        }
        return num;
    }

    static void updateProgramVersion(int searchId, String programVersion) {
        try {
            MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchProgramVersion(searchId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for searchID: "+searchId, e);
        }
    }

    static void updateProgram(int searchId, SearchProgram program) {
        try {
            MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchProgram(searchId, program);
        }
        catch(RuntimeException e) {
            log.warn("Error updating search program for searchID: "+searchId, e);
        }
    }
    
    //--------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    abstract int uploadSearchParameters(String paramFileDirectory, String remoteServer, String remoteDirectory, Date searchDate) throws UploadException;
    
    // NOTE: this method should be called AFTER uploadSearchParameters.
    abstract MsSearchDatabase getSearchDatabase();
    
    abstract SearchProgram getSearchProgram();
    
    abstract void uploadSqtFile(String filePath, int runId) throws UploadException;
    //--------------------------------------------------------------------------------------------------
    
    /**
     * @param fileDirectory 
     * @param fileNames names of sqt files (without the .sqt extension)
     * @param runIdMap map mapping file names to runIds in database
     * @param remoteServer 
     * @param remoteDirectory 
     * @param searchDate 
     */
    public int uploadSearch(String fileDirectory, Set<String> fileNames, Map<String,Integer> runIdMap, String remoteServer, String remoteDirectory, Date searchDate) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numSearchesToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        // parse and upload the search parameters
        try {
            searchId = uploadSearchParameters(fileDirectory, remoteServer, remoteDirectory, searchDate);
        }
        catch (UploadException e) {
            uploadExceptionList.add(e);
            log.error(e.getMessage()+"\n\t!!!SEARCH WILL NOT BE UPLOADED", e);
            return 0;
        }
        
        // get the id of the search database used (will be used to look up protein ids later)
        MsSearchDatabase db = getSearchDatabase();
        String searchDbName = null;
        if (db != null) {
            searchDbName = new File(getSearchDatabase().getServerPath()).getName();
            sequenceDatabaseId = NrSeqLookupUtil.getDatabaseId(searchDbName);
        }
        if (sequenceDatabaseId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.SEARCHDB_NOT_FOUND);
            ex.setErrorMessage("No database ID found for: "+searchDbName);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\t!!!DELETING SEARCH...", ex);
            deleteSearch(searchId);
            return 0;
        }
        
        // initialize the Modification lookup map; will be used when uploading modifications for search results
        dynaModLookup = new DynamicModLookupUtil(searchId);
        
        
        // now upload the individual sqt files
        for (String file: fileNames) {
            String filePath = fileDirectory+File.separator+file+".sqt";
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            Integer runId = runIdMap.get(file); 
            if (runId == null) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SQT);
                uploadExceptionList.add(ex);
                log.error(ex.getMessage()+"\n\tDELETING SEARCH...", ex);
                deleteSearch(searchId);
                return 0;
            }
            resetCaches();
            try {
                uploadSqtFile(filePath, runId);
                numSearchesUploaded++;
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                log.error(ex.getMessage()+"\n\t!!!DELETING SEARCH...", ex);
                deleteSearch(searchId);
                return 0;
            }
        }
        
        // if no sqt files were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            uploadExceptionList.add(ex);
            log.error(ex.getMessage()+"\n\tDELETING SEARCH", ex);
            deleteSearch(searchId);
            searchId = 0;
        }
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (this.programVersion != null && this.programVersion != "uninit") {
            updateProgramVersion(searchId, programVersion);
        }
        
        // if search program from sqt files is not the same as the defalt program returned by the uploader, update it in the msSearch table
        // For now we do this only for SEQUEST and EE-normalized SEQUEST
        if (getSearchProgram() == SearchProgram.SEQUEST && this.programFromSqt == SearchProgram.EE_NORM_SEQUEST) {
            updateProgram(searchId, programFromSqt);
        }
        
        return searchId;
    }
    
    // SEARCH RESULT
    final int uploadSearchHeader(SQTSearchDataProvider provider, int runId, int searchId)
        throws DataProviderException {

        SQTRunSearch search = provider.getSearchHeader();
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
            
            // SEARCH PROGRAM IN THE SQT FILE
            if (programFromSqt == null)
                this.programFromSqt = header.getSearchProgram();
            
            // make sure program is same in all sqt headers
            if (programFromSqt == null || programFromSqt == SearchProgram.UNKNOWN || programFromSqt != header.getSearchProgram()) {
                throw new DataProviderException("Value of SQTGenerator is missing or not the same in all SQT files.");
            }
            
        }
        // save the run search and return the database id
        MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> runSearchDao = daoFactory.getSqtRunSearchDAO();
        return runSearchDao.saveRunSearch(search, runId, searchId);
    }

    final void uploadSearchScan(SQTSearchScan scan, int runSearchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(scan, runSearchId, scanId);
    }

    final int uploadBaseSearchResult(MsSearchResult result, int runSearchId, int scanId) throws UploadException {
        
        MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, result.getResultPeptide().getPeptideSequence(), resultId, sequenceDatabaseId);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);
        
        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);
        
        return resultId;
    }

    // PROTEIN MATCHES
    final void uploadProteinMatches(MsSearchResult result, final String peptide, final int resultId, int databaseId)
        throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchSet.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        for (MsSearchResultProtein match: result.getProteinMatchList()) {
            int proteinId = NrSeqLookupUtil.getProteinId(databaseId, match.getAccession());
            if (proteinId == 0) {
                // try again
                List<Integer> matchingIds = NrSeqLookupUtil.getProteinIdsLikeAccession(databaseId, match.getAccession());
                if (matchingIds.size() == 1)
                    proteinId = matchingIds.get(0);
                else {
                    // last ditch attempt
                    matchingIds = NrSeqLookupUtil.getProteinIdsForPeptide(databaseId, match.getAccession(), peptide);
                    
                    if (matchingIds.size() == 1)
                        proteinId = matchingIds.get(0);
                    
                    // can't do anymore
                    else {
                        UploadException ex = new UploadException(ERROR_CODE.PROTEIN_NOT_FOUND);
                        ex.setErrorMessage("No matching protein found for databaseId: "+databaseId+" and accession: "+match.getAccession());
                        throw ex;
                    }
                }
            }
            
            // NOTE: we are using a Set for the proteinMatches.  ONLY UNIQUE ENTRIES WILL BE ADDED.
            MsSearchResultProteinDbImpl prMatch = new MsSearchResultProteinDbImpl();
            prMatch.setProteinId(proteinId);
            prMatch.setResultId(resultId);
            proteinMatchSet.add(prMatch);
        }
    }
    
    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        List<MsSearchResultProteinDb> list = new ArrayList<MsSearchResultProteinDb>(proteinMatchSet.size());
        list.addAll(proteinMatchSet);
        matchDao.saveAll(list);
        proteinMatchSet.clear();
    }

    // RESIDUE DYNAMIC MODIFICATION
    void uploadResultResidueMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultResidueModIn mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicResidueModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
                        searchId+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIdentifierImpl resultMod = new ResultResidueModIdentifierImpl(resultId, modId, mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }

    private void uploadResultResidueModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicResidueModsForResult(resultResidueModList);
        resultResidueModList.clear();
    }
    
    // TERMINAL DYNAMIC MODIFICATION
    void uploadResultTerminalMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic terminal modifications if the cache has enough entries
        if (resultTerminalModList.size() >= BUF_SIZE) {
            uploadResultTerminalModBuffer();
        }
        // add the dynamic terminal modifications for this result to the cache
        for (MsTerminalModificationIn mod: result.getResultPeptide().getDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicTerminalModificationId(searchId, 
                    mod.getModifiedTerminal(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic terminal modification found for: searchId: "+
                        searchId+
                        "; modTerminal: "+mod.getModifiedTerminal()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultModIdentifierImpl resultMod = new ResultModIdentifierImpl(resultId, modId);
            resultTerminalModList.add(resultMod);
        }
    }

    private void uploadResultTerminalModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
        modDao.saveAllDynamicTerminalModsForResult(resultTerminalModList);
        resultTerminalModList.clear();
    }

    void flush() {
        if (proteinMatchSet.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (resultResidueModList.size() > 0) {
            uploadResultResidueModBuffer();
        }
        if (resultTerminalModList.size() > 0) {
            uploadResultTerminalModBuffer();
        }
    }

    final void deleteLastUploadedRunSearch() {
        if (lastUploadedRunSearchId == 0)
            return;
        MsRunSearchDAO<MsRunSearch, MsRunSearchDb> runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        runSearchDao.deleteRunSearch(lastUploadedRunSearchId);
    }

    public static void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
        searchDao.deleteSearch(searchId);
    }
}