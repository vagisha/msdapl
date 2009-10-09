/**
 * PepXmlSequestDataUploadService.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResultDataWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlSequestFileReader;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.SearchDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatch;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatchingService;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchDatabaseUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchModificationUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchResultProteinUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAO;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PepXmlSequestDataUploadService implements SearchDataUploadService {

    private static final int BUF_SIZE = 500;
    
    private int experimentId;
    private int searchId;
    
    private String dataDirectory;
    private Date searchDate;
    private String remoteServer;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    
    private List<String> searchDataFileNames;
    private List<String> spectrumFileNames;

    private boolean preUploadCheckDone;
    
    private final MsRunUploadDAO runDao;
    private final MsScanUploadDAO scanDao;
    private final MsSearchDatabaseUploadDAO sequenceDbDao;
    private final MsRunSearchUploadDAO runSearchDao;
    private final MsSearchResultProteinUploadDAO proteinMatchDao;
    private final MsSearchModificationUploadDAO modDao;
    private final MsSearchResultUploadDAO resultDao;
    private final MsSearchUploadDAO searchDao;
    private final SequestSearchResultUploadDAO sqtResultDao;
    
    
    // these are the things we will cache and do bulk-inserts
    private List<MsSearchResultProtein> proteinMatchList;
    private List<MsResultResidueModIds> resultResidueModList;
    private List<MsResultTerminalModIds> resultTerminalModList;
    private List<SequestResultDataWId> sequestResultDataList; // sequest scores
    
//    private MsSearchDatabaseIn db = null;
    private boolean usesEvalue = false;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    
    private int sequenceDatabaseId; // nrseq database id
    private DynamicModLookupUtil dynaModLookup;
    private int numSearchesUploaded = 0;
    
    // list of protein peptide matches (if the refresh parser has not been run)
    private Map<String, List<PeptideProteinMatch>> proteinMatches;
    private PeptideProteinMatchingService matchService;
    
    
    private static final Logger log = Logger.getLogger(PepXmlSequestDataUploadService.class.getName());
    
    public PepXmlSequestDataUploadService() {
        
        this.searchDataFileNames = new ArrayList<String>();
        
        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);
        this.sequestResultDataList = new ArrayList<SequestResultDataWId>(BUF_SIZE);
        
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        proteinMatches = new HashMap<String, List<PeptideProteinMatch>>();
        
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        
        this.runDao = daoFactory.getMsRunDAO(); 
        this.scanDao = daoFactory.getMsScanDAO();
        this.sequenceDbDao = daoFactory.getMsSequenceDatabaseDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.sqtResultDao = daoFactory.getSequestResultDAO();
        this.proteinMatchDao = daoFactory.getMsProteinMatchDAO();
        this.modDao = daoFactory.getMsSearchModDAO();
        this.resultDao = daoFactory.getMsSearchResultDAO();
        
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.SEQUEST;
    }
    
    @Override
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public void setSearchDate(java.util.Date date) {
        this.searchDate = date;
    }

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
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
        return "\tSearch file format: "+SearchFileFormat.PEPXML+
        "\n\t#Search files in Directory: "+searchDataFileNames.size()+"; #Uploaded: "+numSearchesUploaded;
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
        
        // 2. Look for *.pep.xml file
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".pep.xml");
            }});
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            name = name.substring(0, name.lastIndexOf(".pep.xml"));
            if(!name.startsWith("interact")) // don't add interact*.pep.xml files here
                searchDataFileNames.add(name);
        }
        
        
        // 3. If we know the raw data file names that will be uploaded match them with up with the 
        //    *.pep.xml file and make sure there is a spectrum data file for each one.
        if(spectrumFileNames != null) {
            for(String file:searchDataFileNames) {
                if(!spectrumFileNames.contains(file)) {
                    appendToMsg("No corresponding spectrum data file found for: "+file);
                    return false;
                }
            }
        }
        
        // 4. Make sure the search parameters file is present
        File paramsFile = new File(dataDirectory+File.separator+searchParamsFile());
        if(!paramsFile.exists()) {
            appendToMsg("Cannot fild search parameters file: "+paramsFile.getAbsolutePath());
            return false;
        }
        
        preUploadCheckDone = true;
        
        return true;
    }

    private String searchParamsFile() {
        SequestParamsParser parser = new SequestParamsParser();
        return parser.paramsFileName();
    }
    
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

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
        
        
        // now upload the search (Sequest) data in the *.pep.xml files
        for (String file: searchDataFileNames) {
            String filePath = dataDirectory+File.separator+file+".pep.xml";
            Integer runId = runIdMap.get(file); 
            
            resetCaches();
            // int runSearchId;
            
            long s = System.currentTimeMillis();
            log.info("Uploading search results in file: "+file);
            PepXmlSequestFileReader parser = new PepXmlSequestFileReader();
            parser.setParseEvalue(this.usesEvalue);
            try {
                parser.open(filePath);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                throw ex;
            }
            
            // there should only be one run_search in this file
            try {
                while(parser.hasNextRunSearch()) {
                    
                    // match the search parameters found in the file against those we uploaded
                    // using the sequest.params file
                    matchSearchParams(searchId, parser);
                
                    try {
                        uploadRunSearch(filePath, searchId, runId, parser);
                        numSearchesUploaded++;
                    }
                    catch (UploadException ex) {
                        ex.appendErrorMessage("\n\tDELETING SEARCH ..."+searchId+"\n");
                        deleteSearch(searchId);
                        numSearchesUploaded = 0;
                    
                        throw ex;
                    }
                }
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                throw ex;
            }
            
            parser.close();
            
            long e = System.currentTimeMillis();
            log.info("Finished uploading search results in file: "+file+"; Time: "+TimeUtils.timeElapsedSeconds(s, e));
            
        }
        
        
        // if no searches were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
            deleteSearch(searchId);
            numSearchesUploaded = 0;
            throw ex;
        }
        
    }
    

    private void matchSearchParams(int searchId, PepXmlSequestFileReader parser) throws UploadException {
        
        // load the search and its parameters, enzyme information, database information
        // and modification information
        SequestSearchDAO searchDao = DAOFactory.instance().getSequestSearchDAO();
        SequestSearch search = searchDao.loadSearch(searchId);
        
        MsSearchIn parsedSearch = parser.getSearch();
        
        SearchParamMatcher matcher = new SearchParamMatcher();
        boolean matches = matcher.matchSearchParams(search, parsedSearch, parser.getRunSearchName());
        
        if(!matches) {
            
            // TODO: if the only thing that mismatched was the enzyme information ignore it
            // pepxml files can have wrong enzyme information. e.g. Trypsin vs Trypsin_K in sequest.params
            if(!matcher.isEnzymesMatch() &&
                (matcher.isDatabasesMatch() && 
                 matcher.isDynamicResidueModsMatch() &&
                 matcher.isStaticResidueModsMatch() && 
                 matcher.isDynamicTerminalModsMatch() &&
                 matcher.isStaticTerminalModsMatch())) {
                log.error(matcher.getErrorMessage());
            }
            else {
                log.error(matcher.getErrorMessage());
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage(matcher.getErrorMessage());
                throw ex;
            }
        }
        // TODO do we need to match some other key parameters e.g. min_enzymatic_termini etc. 
    }

    private int getScanId(int runId, int scanNumber)
            throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    
    private void uploadRunSearch(String filename, int searchId, int runId,
            PepXmlSequestFileReader parser) throws UploadException {
        
        int runSearchId = uploadRunSearchHeader(searchId, runId, parser);
        
        // If the refresh parser has not been run we will initialize the PeptideProteinMatchingService
        if(!parser.isRefreshParserRun()) {
            initializePeptideProteinMatchingService(searchId);
        }
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                PepXmlSequestSearchScanIn scan = parser.getNextSearchScan();
                
                int scanId = getScanId(runId, scan.getScanNumber());
                
                for(SequestPeptideProphetResultIn result: scan.getScanResults()) {
                    // If the refresh parser has not been run, find alternative matches for the peptide
                    if(!parser.isRefreshParserRun()) {
                        
                        SequestSearchResultIn sres = result.getSearchResult();
                        String peptideSeq = sres.getResultPeptide().getPeptideSequence();
                        List<PeptideProteinMatch> matches = proteinMatches.get(peptideSeq);
                        if(matches == null) {
                            matches = matchService.getMatchingProteins(peptideSeq);
                            proteinMatches.put(peptideSeq, matches);
                        }
//                        if(proteinMatches.size() % 10 == 0) {
//                            System.out.println("matches for # sequences: "+proteinMatches.size());
//                        }
                        List<MsSearchResultProteinIn> protList = sres.getProteinMatchList();
                        
                        for(PeptideProteinMatch match: matches) {
                            boolean haveAlready = false;
                            for(MsSearchResultProteinIn prot: protList) {
                                if(match.getProtein().getAccessionString().equals(prot.getAccession())) { // this one we have already
                                    haveAlready = true;
                                    break;
                                }
                            }
                            if(haveAlready)
                                continue;
                            DbLocus locus = new DbLocus(match.getProtein().getAccessionString(), match.getProtein().getDescription());
                            locus.setNtermResidue(match.getPreResidue());
                            locus.setCtermResidue(match.getPostResidue());
                            locus.setNumEnzymaticTermini(match.getNumEnzymaticTermini());
                            sres.addMatchingProteinMatch(locus);

                        }
                    }
                    int resultId = uploadBaseSearchResult(result.getSearchResult(), runSearchId, scanId);
                    uploadSequestResultData(result.getSearchResult().getSequestResultData(), resultId); // Sequest scores
                    numResults++;
                }
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            throw ex;
        }
        
        flush(); // save any cached data
        log.info("Uploaded search results for file: "+filename+", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");
        
    }
    
    private void initializePeptideProteinMatchingService(int searchId) throws UploadException {
        
        if(this.matchService != null)
            return;
        
        // get the search
        MsSearch search = searchDao.loadSearch(searchId);
        List<MsEnzyme> enzymes = search.getEnzymeList();
        List<MsSearchDatabase> databases = search.getSearchDatabases();
        
        int numEnzymaticTermini = 0;
        if(search.getSearchProgram() == Program.SEQUEST) {
            SequestSearchDAO seqDao = DAOFactory.instance().getSequestSearchDAO();
            numEnzymaticTermini = seqDao.getNumEnzymaticTermini(searchId);
        }
        // TODO what about other search engines
        
        if(databases.size() != 1) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage("Multiple search databases found for search: "+
                    searchId+
                    "; PeptideProteinMatchingService does not handle multiple databases");
            throw ex; 
        }
        try {
            this.matchService = new PeptideProteinMatchingService(databases.get(0).getSequenceDatabaseId());
        }
        catch (SQLException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.setErrorMessage("Error initializing PeptideProteinMatchingService for databaseID: "+
                    databases.get(0).getSequenceDatabaseId());
            ex.appendErrorMessage(e.getMessage());
            throw ex; 
        }
        
        matchService.setNumEnzymaticTermini(numEnzymaticTermini);
        matchService.setEnzymes(enzymes);
    }

    private void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (resultResidueModList.size() > 0) {
            uploadResultResidueModBuffer();
        }
        if (resultTerminalModList.size() > 0) {
            uploadResultTerminalModBuffer();
        }
        if (sequestResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
    }

    // -------------------------------------------------------------------------------------------
    // UPLOAD SEQUEST SCORES
    // -------------------------------------------------------------------------------------------
    private void uploadSequestResultData(SequestResultData resultData, int resultId) {
        // upload the Sequest specific result information if the cache has enough entries
        if (sequestResultDataList.size() >= BUF_SIZE) {
            uploadSequestResultBuffer();
        }
        // add the Sequest specific information for this result to the cache
        SequestResultDataWrap resultDataDb = new SequestResultDataWrap(resultData, resultId);
        sequestResultDataList.add(resultDataDb);
    }
    
    private void uploadSequestResultBuffer() {
        sqtResultDao.saveAllSequestResultData(sequestResultDataList);
        sequestResultDataList.clear();
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD A SINGLE SEARCH RESULT
    // -------------------------------------------------------------------------------------------
    private int uploadBaseSearchResult(MsSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY
        
        // upload the protein matches
        uploadProteinMatches(result, resultId, sequenceDatabaseId);
        
        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);
        
        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);
        
        return resultId;
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD A LIST OF SEARCH RESULTS
    // -------------------------------------------------------------------------------------------
    private <T extends MsSearchResult> List<Integer> uploadBaseSearchResults(List<T> results) throws UploadException {
        
        List<Integer> autoIncrIds = resultDao.saveResultsOnly(results);
        for(int i = 0; i < results.size(); i++) {
            MsSearchResult result = results.get(i);
            int resultId = autoIncrIds.get(i);
            
            // upload the protein matches
            uploadProteinMatches(result, resultId);
            
            // upload dynamic mods for this result
            uploadResultResidueMods(result, resultId, result.getRunSearchId());
            
            // no dynamic terminal mods for sequest
            uploadResultTerminalMods(result, resultId, searchId);
        }
        
        return autoIncrIds;
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD PROTEIN MATCHES
    // -------------------------------------------------------------------------------------------
    private void uploadProteinMatches(MsSearchResultIn result, final int resultId, int databaseId)
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
    
    private final void uploadProteinMatches(MsSearchResult result, final int resultId)
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
            proteinMatchList.add(new SearchResultProteinBean(result.getId(), match.getAccession()));
        }
    }
    private void uploadProteinMatchBuffer() {
        
        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        proteinMatchDao.saveAll(list);
        proteinMatchList.clear();
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD RESIDUE DYNAMIC MODIFICATION
    // -------------------------------------------------------------------------------------------
    private void uploadResultResidueMods(MsSearchResultIn result, int resultId, int searchId) throws UploadException {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            MsResidueModification modMatch = dynaModLookup.getDynamicResidueModification(
                                        mod.getModifiedResidue(),
                                        mod.getModificationMass(), false);
            if (modMatch == null) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
                        searchId+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modMatch.getId(), mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }
    
    private void uploadResultResidueMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
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
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD TERMINAL DYNAMIC MODIFICATION
    // -------------------------------------------------------------------------------------------
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
    
    private void uploadResultTerminalMods(MsSearchResult result, int resultId, int searchId) throws UploadException {
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
    
    
    // -------------------------------------------------------------------------------
    // UPLOAD DATA INTO THE msRunSearch TABLE
    // -------------------------------------------------------------------------------
    private int uploadRunSearchHeader(int searchId, int runId,
            PepXmlSequestFileReader parser) throws UploadException {
        
        MsRunSearchIn runSearch;
        try {
            runSearch = parser.getRunSearchHeader();
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(runSearch instanceof RunSearchBean) {
            RunSearchBean rsb = (RunSearchBean) runSearch;
            rsb.setRunId(runId);
            rsb.setSearchId(searchId);
            rsb.setSearchDate(new java.sql.Date(searchDate.getTime()));
            return runSearchDao.saveRunSearch(rsb);
        }
        else {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR);
            ex.setErrorMessage("Invalid header type for run search");
            throw ex;
        }
    }
    
    private Map<String, Integer> createRunIdMap() throws UploadException {
        
        Map<String, Integer> runIdMap = new HashMap<String, Integer>(searchDataFileNames.size()*2);
        for(String file: searchDataFileNames) {
            int runId = 0;
            try {runId = runDao.loadRunIdForExperimentAndFileName(experimentId, file);}
            catch(Exception e) {
                UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR);
                throw ex;
            }
            if(runId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
                ex.appendErrorMessage("File: "+file);
                throw ex;
            }
            runIdMap.put(file, runId);
        }
        return runIdMap;
    }

    private int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        SequestParamsParser parser = parseSequestParams(paramFileDirectory, remoteServer);
        
        usesEvalue = parser.reportEvalue();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        
        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(parser.getSearchDatabase());
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            SequestSearchUploadDAO searchDAO = UploadDAOFactory.getInstance().getSequestSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, Program.SEQUEST,
                    remoteDirectory, searchDate), experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private SequestParamsParser parseSequestParams(String fileDirectory, final String remoteServer) throws UploadException {
        
        // parse the parameters file
        final SequestParamsParser parser = new SequestParamsParser();
        log.info("BEGIN Sequest search UPLOAD -- parsing parameters file: "+parser.paramsFileName());
        if (!(new File(fileDirectory+File.separator+parser.paramsFileName()).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SEQUEST_PARAMS);
            throw ex;
        }
        try {
            parser.parseParams(remoteServer, fileDirectory);
            return parser;
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(fileDirectory+File.separator+parser.paramsFileName());
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    // get the id of the search database used (will be used to look up protein ids later)
    private int getSearchDatabaseId(MsSearchDatabaseIn db) throws UploadException {
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
    
    private static SequestSearchIn makeSearchObject(final SequestParamsParser parser, final Program searchProgram,
                final String remoteDirectory, final java.util.Date searchDate) {
        
        return new SequestSearchIn() {
            @Override
            public List<Param> getSequestParams() {return parser.getParamList();}
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
            public Program getSearchProgram() {return searchProgram;}
//            public Program getSearchProgram() {return parser.getSearchProgram();}
            @Override
            public String getSearchProgramVersion() {return null;} // we don't have this information in sequest.params
            public java.sql.Date getSearchDate() {return new java.sql.Date(searchDate.getTime());}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesUploaded = 0;

        resetCaches();

        searchId = 0;
        sequenceDatabaseId = 0;
        
        preUploadCheckMsg = new StringBuilder();
        
        usesEvalue = false;
        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    // called before uploading each msms_run_search in the interact.pep.xml file and in the reset() method.
    private void resetCaches() {
        
        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
        sequestResultDataList.clear();
    }
    
    
    @Override
    public void checkResultChargeMass(boolean check) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDecoyDirectory(String directory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteDecoyDirectory(String directory) {
        throw new UnsupportedOperationException();
    }
    
    public void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        log.info("Deleting search ID: "+searchId);
        searchDao.deleteSearch(searchId);
    }

    @Override
    public int getUploadedSearchId() {
        return this.searchId;
    }

    @Override
    public List<String> getFileNames() {
        return this.searchDataFileNames;
    }
    
    public static void main(String[] args) throws UploadException {
        PepXmlSequestDataUploadService p = new PepXmlSequestDataUploadService();
        List<String> spectrumFileNames = new ArrayList<String>();
        
        spectrumFileNames.add("M_102908_Y_Lys_ETD_EPI_contol");
        spectrumFileNames.add("M_102908_Y_Lys_ETD_EPI_poly2");
        spectrumFileNames.add("M_121808_Yeast_LysC_ETD_EPIQ_01");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_01");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_02");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_03");
        
        p.setSpectrumFileNames(spectrumFileNames);
        
        p.setDirectory("/Users/silmaril/WORK/UW/FLINT/Jimmy_Test");
        p.setSearchDate(new Date());
        p.setExperimentId(37);
        p.upload();
    }
}
