package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
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
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public abstract class AbstractSQTDataUploadService {

    static final Logger log = Logger.getLogger(AbstractSQTDataUploadService.class);

    static final DAOFactory daoFactory = DAOFactory.instance();

    private DynamicModLookupUtil dynaModLookup;

    static final int BUF_SIZE = 500;
    
    private static final double HYDROGEN = 1.00794;

    // these are the things we will cache and do bulk-inserts
    List<MsSearchResultProtein> proteinMatchList;
    List<MsResultResidueModIds> resultResidueModList;
    List<MsResultTerminalModIds> resultTerminalModList;
    Map<String, SQTSearchScan> searchScanMap;


    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();

    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msSearch and msSequenceDatabaseDetail table.
    private String programVersion = "uninit";
    private Program programFromSqt = null;

    int lastUploadedRunSearchId;
    int searchId;
    int sequenceDatabaseId; // nrseq database id
    
    public AbstractSQTDataUploadService() {
        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);
        this.searchScanMap = new HashMap<String, SQTSearchScan>((int) (BUF_SIZE*1.5));
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
        
        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
        searchScanMap.clear();

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

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
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
            MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchProgramVersion(searchId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for searchID: "+searchId, e);
        }
    }

    static void updateProgram(int searchId, Program program) {
        try {
            MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
            searchDao.updateSearchProgram(searchId, program);
        }
        catch(RuntimeException e) {
            log.warn("Error updating search program for searchID: "+searchId, e);
        }
    }
    
    //--------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    abstract int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory, Date searchDate) throws UploadException;
    
    // NOTE: this method should be called AFTER uploadSearchParameters.
    abstract MsSearchDatabaseIn getSearchDatabase();
    
    abstract Program getSearchProgram();
    
    abstract void uploadSqtFile(String filePath, int runId) throws UploadException;
    
    abstract void removeCacheForResultIds(List<Integer> resultIds);
    //--------------------------------------------------------------------------------------------------
    
    /**
     * @param fileDirectory 
     * @param fileNames names of sqt files (without the .sqt extension)
     * @param runIdMap map mapping file names to runIds in database
     * @param remoteServer 
     * @param remoteDirectory 
     * @param searchDate 
     */
    public int uploadSearch(int experimentId, String fileDirectory, Set<String> fileNames,
            Map<String,Integer> runIdMap, 
            String remoteServer, String remoteDirectory, Date searchDate) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numSearchesToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        // parse and upload the search parameters
        try {
            searchId = uploadSearchParameters(experimentId, fileDirectory, 
                    remoteServer, remoteDirectory, searchDate);
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
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
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                return 0;
            }
            resetCaches();
            try {
                uploadSqtFile(filePath, runId);
                numSearchesUploaded++;
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                log.error(ex.getMessage(), ex);
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                return 0;
            }
        }
        
        // if no sqt files were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            uploadExceptionList.add(ex);
            ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
            log.error(ex.getMessage(), ex);
            deleteSearch(searchId);
            numSearchesUploaded = 0;
            return 0;
        }
        
        // Update the "analysisProgramVersion" in the msSearch table
        if (this.programVersion != null && this.programVersion != "uninit") {
            updateProgramVersion(searchId, programVersion);
        }
        
        // if search program from sqt files is not the same as the defalt program returned by the uploader, update it in the msSearch table
        // For now we do this only for SEQUEST and EE-normalized SEQUEST
        if (getSearchProgram() == Program.SEQUEST && this.programFromSqt == Program.EE_NORM_SEQUEST) {
            updateProgram(searchId, programFromSqt);
        }
        
        return searchId;
    }
    
    // get the id of the search database used (will be used to look up protein ids later)
    int getSearchDatabaseId(MsSearchDatabaseIn db) throws UploadException {
        String searchDbName = null;
        int dbId = 0;
        if (db != null) {
            searchDbName = getSearchDatabase().getDatabaseFileName();
            dbId = NrSeqLookupUtil.getDatabaseId(searchDbName);
        }
        if (dbId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.SEARCHDB_NOT_FOUND);
            ex.setErrorMessage("No database ID found for: "+searchDbName);
            throw ex;
        }
        return dbId;
    }
    
    // RUN SEARCH
    final int uploadSearchHeader(SQTSearchDataProvider provider, int runId, int searchId)
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
            
            // SEARCH PROGRAM IN THE SQT FILE
            if (programFromSqt == null)
                this.programFromSqt = header.getSearchProgram();
            
            // make sure program is same in all sqt headers
            if (programFromSqt == null || programFromSqt == Program.UNKNOWN || programFromSqt != header.getSearchProgram()) {
                throw new DataProviderException("Value of SQTGenerator is missing or not the same in all SQT files.");
            }
            
        }
        // save the run search and return the database id
        SQTRunSearchDAO runSearchDao = daoFactory.getSqtRunSearchDAO();
        return runSearchDao.saveRunSearch(new SQTRunSearchWrap(search, searchId, runId));
    }

    private SQTSearchScan getOldScanIfExists(int runSearchId, int scanId, int charge) {
        // look in the cache first
        SQTSearchScan scan = searchScanMap.get(scanId+"_"+charge);
        if(scan != null)   return scan;
        // now look in the database
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        return spectrumDataDao.load(runSearchId, scanId, charge);
    }
    
    private void deleteOldSearchScan(int runSearchId, int scanId, int charge) {
        // look in the cache first
        SQTSearchScan scan = searchScanMap.get(scanId+"_"+charge);
        if(scan != null)
            searchScanMap.remove(scanId+"_"+charge);
        // if it is not found in the cache remove from the database
        else {
            SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
            spectrumDataDao.delete(runSearchId, scanId, charge);
        }
    }
    
    /**
     * 
     * @param scan
     * @param runSearchId
     * @param scanId
     * @return true if the search scan got uploaded, false otherwise
     */
    final boolean uploadSearchScan(SQTSearchScanIn scan, int runSearchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        
        // NOTE: Added some changes to deal with duplicate results in MacCoss lab data
        int charge = scan.getCharge();
        SQTSearchScan oldScan = getOldScanIfExists(runSearchId, scanId, charge);
        if(oldScan == null) {
            uploadSearchScan(new SQTSearchScanWrap(scan, runSearchId, scanId));
//            spectrumDataDao.save(new SQTSearchScanWrap(scan, runSearchId, scanId));
            return true;
        }
        
        log.info("Found duplicate result for runSearchId: "+runSearchId+", scanID: "+scanId+" and charge: "+charge);
        // If we already have results with with the same scanID and charge we will 
        // keep the "better" results.
        
        // First check if both results are the same. If they are don't upload this one.
        if(oldScan.getObservedMass().doubleValue() == scan.getObservedMass().doubleValue()) {
            log.info("\tResults are the same. Keeping the old one...");
            return false;
        }
        
        // To determine which is a better result, look at the observedMass for the 
        // old searchScan and the new searchScan.  Keep the one where the observedMass
        // is closer to the preMZ of the scan
        MsScan msScan = DAOFactory.instance().getMsScanDAO().load(scanId);
        double premz = msScan.getPrecursorMz().doubleValue();
        log.info("\tPrecursor M/Z: "+premz+"; old mass: "+oldScan.getObservedMass()+"; new mass: "+scan.getObservedMass());
        double peptideMH = (premz - HYDROGEN) * charge + HYDROGEN;
        double oldDiff = Math.abs(peptideMH - oldScan.getObservedMass().doubleValue());
        double newDiff = Math.abs(peptideMH - scan.getObservedMass().doubleValue());
        if(newDiff < oldDiff) {
            // first delete old results
            deleteOldSearchScan(runSearchId, scanId, charge);
//            spectrumDataDao.delete(runSearchId, scanId, charge);
            List<Integer> resultIdsToDelete = resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
            resultDao.deleteResults(runSearchId, scanId, charge);
            
            // If there are cahced entries for this resultIds, remove them
            removeCachedResultIds(resultIdsToDelete);
            
            // save the new result
            uploadSearchScan(new SQTSearchScanWrap(scan, runSearchId, scanId));
//            spectrumDataDao.save(new SQTSearchScanWrap(scan, runSearchId, scanId));
            log.info("\tNEW result is better");
            return true;
        }
        else {
            log.info("\tOLD result was better");
            return false;
        }
    }

    // If there is cached data for the following result Ids remove it
    private void removeCachedResultIds(List<Integer> resultIds) {
        
        log.info("\tRemoving cached entries for "+resultIds.size()+" resultIds");
        Iterator<MsSearchResultProtein> iter = proteinMatchList.iterator();
        while(iter.hasNext()) {
            MsSearchResultProtein prot = iter.next();
            if(resultIds.contains(prot.getResultId()))
                iter.remove();
        }
        
        Iterator<MsResultResidueModIds> rmodIter = resultResidueModList.iterator();
        while(rmodIter.hasNext()) {
            MsResultResidueModIds mod = rmodIter.next();
            if(resultIds.contains(mod.getResultId()))
                iter.remove();
        }
        
        Iterator<MsResultTerminalModIds> tmodIter = resultTerminalModList.iterator();
        while(tmodIter.hasNext()) {
            MsResultTerminalModIds mod = tmodIter.next();
            if(resultIds.contains(mod.getResultId()))
                iter.remove();
        }
        
        // subclasses should also clear any cached data with these resultIds
        removeCacheForResultIds(resultIds);
    }
    
    
    final int uploadBaseSearchResult(MsSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, result.getResultPeptide().getPeptideSequence(), resultId, sequenceDatabaseId);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);
        
        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);
        
        return resultId;
    }

    // SEARCH SCAN
    final void uploadSearchScan(SQTSearchScan searchScan) {
        // if the cache has enough entries upload 
        if(searchScanMap.size() >= BUF_SIZE) {
            uploadSearchScanBuffer();
        }
        String key = searchScan.getScanId()+"_"+searchScan.getCharge();
        searchScanMap.put(key, searchScan);
    }
    
    private void uploadSearchScanBuffer() {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.saveAll(new ArrayList(this.searchScanMap.values()));
        searchScanMap.clear();
    }
    
    // PROTEIN MATCHES
    final void uploadProteinMatches(MsSearchResultIn result, final String peptide, final int resultId, int databaseId)
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
    
    private void uploadProteinMatchBuffer() {
        MsSearchResultProteinDAO matchDao = daoFactory.getMsProteinMatchDAO();
        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        matchDao.saveAll(list);
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
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modId, mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }

    private void uploadResultResidueModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
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
            ResultTerminalModIds resultMod = new ResultTerminalModIds(resultId, modId);
            resultTerminalModList.add(resultMod);
        }
    }

    private void uploadResultTerminalModBuffer() {
        MsSearchModificationDAO modDao = daoFactory.getMsSearchModDAO();
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

    final void deleteLastUploadedRunSearch() {
        if (lastUploadedRunSearchId == 0)
            return;
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        runSearchDao.deleteRunSearch(lastUploadedRunSearchId);
    }

    public static void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        searchDao.deleteSearch(searchId);
    }
}