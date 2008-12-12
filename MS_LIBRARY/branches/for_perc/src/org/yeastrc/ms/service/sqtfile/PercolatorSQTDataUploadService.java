/**
 * PercolatorSQTDataUploadService.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTRunSearchWrap;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class PercolatorSQTDataUploadService {
    
    
    static final Logger log = Logger.getLogger(PercolatorSQTDataUploadService.class);

    static final DAOFactory daoFactory = DAOFactory.instance();

    private DynamicModLookupUtil dynaModLookup;

    static final int BUF_SIZE = 1000;

    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    
    // these are the things we will cache and do bulk-inserts
    private List<PercolatorResultDataWId> percolatorResultDataList; // percolator scores


    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();

    private int numSearchesToUpload = 0;
    private int numSearchesUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msPostSearchAnalysis
    private String programVersion = "uninit";

    int lastUploadedRunSearchId;
    int searchId;
    
    public PercolatorSQTDataUploadService(int searchId) {
        this.searchId = searchId;
        this.uploadExceptionList = new ArrayList<UploadException>();
        this.percolatorResultDataList = new ArrayList<PercolatorResultDataWId>(BUF_SIZE);
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
    }
    
    void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesToUpload = 0;
        numSearchesUploaded = 0;

        resetCaches();

        uploadExceptionList.clear();
        programVersion = "uninit";
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    // called before uploading each sqt file and in the reset() method.
    void resetCaches() {
        percolatorResultDataList.clear();
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

    static void updateProgram(int searchId, SearchProgram program) {
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
    public int uploadSearch(int experimentId, String fileDirectory, Set<String> fileNames,
            Map<String,Integer> runIdMap, 
            String remoteServer, String remoteDirectory, Date searchDate) {

        reset();// reset all caches etc.
        
        // get the number of sqt file in the directory
        this.numSearchesToUpload = getNumFilesToUpload(fileDirectory, fileNames);
        
        
        // initialize the Modification lookup map; will be used when parsing the peptides
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
        if (getSearchProgram() == SearchProgram.SEQUEST && this.programFromSqt == SearchProgram.EE_NORM_SEQUEST) {
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
            if (programFromSqt == null || programFromSqt == SearchProgram.UNKNOWN || programFromSqt != header.getSearchProgram()) {
                throw new DataProviderException("Value of SQTGenerator is missing or not the same in all SQT files.");
            }
            
        }
        // save the run search and return the database id
        SQTRunSearchDAO runSearchDao = daoFactory.getSqtRunSearchDAO();
        return runSearchDao.saveRunSearch(new SQTRunSearchWrap(search, searchId, runId));
    }

    final void uploadSearchScan(SQTSearchScanIn scan, int runSearchId, int scanId) {
        SQTSearchScanDAO spectrumDataDao = DAOFactory.instance().getSqtSpectrumDAO();
        spectrumDataDao.save(new SQTSearchScanWrap(scan, runSearchId, scanId));
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // these are the things we will cache and do bulk-inserts
    
    

    SearchProgram getSearchProgram() {
        return SearchProgram.PERCOLATOR;
    }
    
    @Override
    int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
       throw new UnsupportedOperationException("PercolatorSQTDataUploadService does not support uploadSearchParameters()");
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
            uploadSequestSqtFile(provider, searchId, runId, sequenceDatabaseId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            ex.appendErrorMessage("\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
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
            for (SequestSearchResultIn result: scan.getScanResults()) {
                uploadSearchResult(result, lastUploadedRunSearchId, scanId);
                numResults++;
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results. (runSearchId: "+lastUploadedRunSearchId+")");
                
    }

    
    private SequestSearchIn makeSearchObject(final SequestParamsParser parser, final String remoteDirectory, final Date searchDate) {
        return new SequestSearchIn() {
            @Override
            public List<SequestParam> getSequestParams() {return parser.getParamList();}
            @Override
            public List<MsResidueModificationIn> getDynamicResidueMods() {return parser.getDynamicResidueMods();}
            @Override
            public List<MsTerminalModificationIn> getDynamicTerminalMods() {return parser.getDynamicTerminalMods();}
            @Override
            public List<MsEnzymeIn> getEnzymeList() {
                if (parser.isEnzymeUsedForSearch())
                    return Arrays.asList(new MsEnzymeIn[]{parser.getSearchEnzyme()});
                else 
                    return new ArrayList<MsEnzymeIn>(0);
            }
            @Override
            public List<MsSearchDatabaseIn> getSearchDatabases() {return Arrays.asList(new MsSearchDatabaseIn[]{parser.getSearchDatabase()});}
            @Override
            public List<MsResidueModificationIn> getStaticResidueMods() {return parser.getStaticResidueMods();}
            @Override
            public List<MsTerminalModificationIn> getStaticTerminalMods() {return parser.getStaticTerminalMods();}
            @Override
            public SearchProgram getSearchProgram() {return parser.getSearchProgram();}
            @Override
            public String getSearchProgramVersion() {return null;} // we don't have this information in sequest.params
            public Date getSearchDate() {return searchDate;}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void uploadSearchResult(SequestSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = super.uploadBaseSearchResult(result, runSearchId, scanId);
        
        // upload the SQT sequest specific information for this result.
        uploadSequestResultData(result.getSequestResultData(), resultId);
    }

    private void uploadSequestResultData(SequestResultData resultData, int resultId) {
        // upload the Sequest specific result information if the cache has enough entries
        if (percolatorResultDataList.size() >= BUF_SIZE) {
            uploadSequestResultBuffer();
        }
        // add the Sequest specific information for this result to the cache
        ResultData resultDataDb = new ResultData(resultId, resultData);
        percolatorResultDataList.add(resultDataDb);
    }
    
    private void uploadSequestResultBuffer() {
        SequestSearchResultDAO sqtResultDao = daoFactory.getSequestResultDAO();
        sqtResultDao.saveAllSequestResultData(percolatorResultDataList);
        percolatorResultDataList.clear();
    }
    
    void flush() {
        super.flush();
        if (percolatorResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
    }
    
}
