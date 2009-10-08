/**
 * PepXmlMascotDataUploadService.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.MascotPeptideProphetResultIn;
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
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.mascot.MascotResultData;
import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;
import org.yeastrc.ms.domain.search.mascot.impl.MascotResultDataWrap;
import org.yeastrc.ms.domain.search.pepxml.mascot.PepXmlMascotSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlMascotFileReader;
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
import org.yeastrc.ms.upload.dao.search.mascot.MascotSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.mascot.MascotSearchUploadDAO;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PepXmlMascotDataUploadService implements SearchDataUploadService {

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
    private final MascotSearchResultUploadDAO mascotResultDao;
    
    
    // these are the things we will cache and do bulk-inserts
    private List<MsSearchResultProtein> proteinMatchList;
    private List<MsResultResidueModIds> resultResidueModList;
    private List<MsResultTerminalModIds> resultTerminalModList;
    private List<MascotResultDataWId> mascotResultDataList; // mascot scores
    
    private MsSearchDatabaseIn db = null;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    
    private int sequenceDatabaseId; // nrseq database id
    private DynamicModLookupUtil dynaModLookup;
    private int numSearchesUploaded = 0;
    
    // list of protein peptide matches (if the refresh parser has not been run)
    private Map<String, List<PeptideProteinMatch>> proteinMatches;
    private PeptideProteinMatchingService matchService;
    
    
    private static final Logger log = Logger.getLogger(PepXmlMascotDataUploadService.class.getName());
    
    public PepXmlMascotDataUploadService() {
        
        this.searchDataFileNames = new ArrayList<String>();
        
        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);
        this.mascotResultDataList = new ArrayList<MascotResultDataWId>(BUF_SIZE);
        
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        proteinMatches = new HashMap<String, List<PeptideProteinMatch>>();
        
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        
        this.runDao = daoFactory.getMsRunDAO(); 
        this.scanDao = daoFactory.getMsScanDAO();
        this.sequenceDbDao = daoFactory.getMsSequenceDatabaseDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.mascotResultDao = daoFactory.getMascotResultDAO();
        this.proteinMatchDao = daoFactory.getMsProteinMatchDAO();
        this.modDao = daoFactory.getMsSearchModDAO();
        this.resultDao = daoFactory.getMsSearchResultDAO();
        
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
        
        preUploadCheckDone = true;
        
        return true;
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
        
        
        searchId = 0;
        
        boolean firstFile = true;
        // now upload the search (Mascot) data in the *.pep.xml files
        for (String file: searchDataFileNames) {
            String filePath = dataDirectory+File.separator+file+".pep.xml";
            Integer runId = runIdMap.get(file); 
            
            resetCaches();
            // int runSearchId;
            
            long s = System.currentTimeMillis();
            log.info("Uploading search results in file: "+file);
            PepXmlMascotFileReader parser = new PepXmlMascotFileReader();
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
                    
                    if(firstFile) {
                        firstFile = false;
                        // We do not a parameters file for a Mascot Search. So we will read the search parameter
                        // from the first pep.xml file
                        try {
                            searchId = uploadSearch(experimentId, parser, dataDirectory, 
                                    remoteServer, remoteDirectory, searchDate);
                        }
                        catch (UploadException e) {
                            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
                            throw e;
                        }
                        // initialize the Modification lookup map; will be used when uploading modifications for search results
                        dynaModLookup = new DynamicModLookupUtil(searchId);
                    }
                    else {
                        // match the search parameters found in the file against those we uploaded
                        // using the first pep.xml file
                        matchSearchParams(searchId, parser.getSearch(), parser.getRunSearchName());
                    }

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
    

    private void matchSearchParams(int searchId, MsSearchIn parsedSearch, String fileName) throws UploadException {
        
        // load the search and its parameters, enzyme information, database information
        // and modification information
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        // match enzyme information
        List<MsEnzyme> uploadedEnzymes = search.getEnzymeList();
        List<MsEnzymeIn> enzymes = parsedSearch.getEnzymeList();
        matchEnzymes(uploadedEnzymes, enzymes, fileName);
        
        // match database information
        List<MsSearchDatabase> uploadedDbs = search.getSearchDatabases();
        List<MsSearchDatabaseIn> databases = parsedSearch.getSearchDatabases();
        matchDatabases(uploadedDbs, databases, fileName);
        
        // match dynamic residue modification information
        List<MsResidueModification> uploadedDynaResMods = search.getDynamicResidueMods();
        List<MsResidueModificationIn> dynaResMods = parsedSearch.getDynamicResidueMods();
        matchResidueModifictions(uploadedDynaResMods, dynaResMods, fileName);
        
        // match static residue modification information
        List<MsResidueModification> uploadedStaticResMods = search.getStaticResidueMods();
        List<MsResidueModificationIn> staticResMods = parsedSearch.getStaticResidueMods();
        matchResidueModifictions(uploadedStaticResMods, staticResMods, fileName);
        
        // match dynamic terminal modification information
        List<MsTerminalModification> uploadedDynaTermMods = search.getDynamicTerminalMods();
        List<MsTerminalModificationIn> dynaTermMods = parsedSearch.getDynamicTerminalMods();
        matchTerminalModifictions(uploadedDynaTermMods, dynaTermMods, fileName);
        
        // match dynamic terminal modification information
        List<MsTerminalModification> uploadedStaticTermMods = search.getStaticTerminalMods();
        List<MsTerminalModificationIn> dynaStaticMods = parsedSearch.getStaticTerminalMods();
        matchTerminalModifictions(uploadedStaticTermMods, dynaStaticMods, fileName);
        
        // TODO do we need to match some other key parameters e.g. min_enzymatic_termini etc. 
    }

    private void matchTerminalModifictions(
            List<MsTerminalModification> uploadedDynaTermMods,
            List<MsTerminalModificationIn> dynaTermMods, String fileName) throws UploadException {

        if(uploadedDynaTermMods.size() != uploadedDynaTermMods.size()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage("Number of uploaded terminal modification : "+uploadedDynaTermMods.size()+
                    " does not match # found in file "+fileName+": "+uploadedDynaTermMods.size());
            throw ex;
        }
        
        if(uploadedDynaTermMods.size() == 0)
            return;
        
        Collections.sort(uploadedDynaTermMods, new Comparator<MsTerminalModification>() {
            public int compare(MsTerminalModification o1, MsTerminalModification o2) {
                int val = o1.getModifiedTerminal().compareTo(o2.getModifiedTerminal());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        Collections.sort(uploadedDynaTermMods, new Comparator<MsTerminalModificationIn>() {
            public int compare(MsTerminalModificationIn o1, MsTerminalModificationIn o2) {
                int val = o1.getModifiedTerminal().compareTo(o2.getModifiedTerminal());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        
        for(int i = 0; i < uploadedDynaTermMods.size(); i++) {
            if(!matchTerminalModification(uploadedDynaTermMods.get(i), uploadedDynaTermMods.get(i))) {
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage("Mismatch in uploaded terminal modification and modification in file: "+fileName);
                throw ex;
            }
        }
    }
    
    private boolean matchTerminalModification(MsTerminalModification mod1, MsTerminalModificationIn mod2) {
        
        if(mod1.getModifiedTerminal() != mod2.getModifiedTerminal())
            return false;
        if(!mod1.getModificationMass().equals(mod2.getModificationMass()))
            return false;
        return true;
    }


    private void matchResidueModifictions(
            List<MsResidueModification> uploadedDynaResMods,
            List<MsResidueModificationIn> dynaResMods, String fileName) throws UploadException {
        
        if(uploadedDynaResMods.size() != dynaResMods.size()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage("Number of uploaded residue modification : "+uploadedDynaResMods.size()+
                    " does not match # found in file "+fileName+": "+dynaResMods.size());
            throw ex;
        }
        
        if(uploadedDynaResMods.size() == 0)
            return;
        
        Collections.sort(uploadedDynaResMods, new Comparator<MsResidueModification>() {
            public int compare(MsResidueModification o1, MsResidueModification o2) {
                int val = Character.valueOf(o1.getModifiedResidue()).compareTo(o2.getModifiedResidue());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        Collections.sort(dynaResMods, new Comparator<MsResidueModificationIn>() {
            public int compare(MsResidueModificationIn o1, MsResidueModificationIn o2) {
                int val = Character.valueOf(o1.getModifiedResidue()).compareTo(o2.getModifiedResidue());
                if(val != 0) return val;
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        
        for(int i = 0; i < uploadedDynaResMods.size(); i++) {
            if(!matchResidueModification(uploadedDynaResMods.get(i), dynaResMods.get(i))) {
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage("Mismatch in uploaded residue modification and modification in file: "+fileName);
                throw ex;
            }
        }
    }
    
    private boolean matchResidueModification(MsResidueModification mod1, MsResidueModificationIn mod2) {
        
        if(mod1.getModifiedResidue() != mod2.getModifiedResidue())
            return false;
        if(mod1.getModificationSymbol() != mod2.getModificationSymbol())
            return false;
        if(!mod1.getModificationMass().equals(mod2.getModificationMass()))
            return false;
        return true;
    }

    private void matchDatabases(List<MsSearchDatabase> uploadedDbs,
            List<MsSearchDatabaseIn> databases, String fileName) throws UploadException {
        
        if(uploadedDbs.size() != databases.size()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage("Number of uploaded search databases : "+uploadedDbs.size()+
                    " does not match # databases in file "+fileName+": "+databases.size());
            throw ex;
        }
        
        Collections.sort(uploadedDbs, new Comparator<MsSearchDatabase>() {
            public int compare(MsSearchDatabase o1, MsSearchDatabase o2) {
                return o1.getDatabaseFileName().compareTo(o2.getDatabaseFileName());
            }});
        Collections.sort(databases, new Comparator<MsSearchDatabaseIn>() {
            public int compare(MsSearchDatabaseIn o1, MsSearchDatabaseIn o2) {
                return o1.getDatabaseFileName().compareTo(o2.getDatabaseFileName());
            }});
        for(int i = 0; i < uploadedDbs.size(); i++) {
            if(!uploadedDbs.get(i).getDatabaseFileName().equals(databases.get(i).getDatabaseFileName())) {
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage("Mismatch in uploaded database database in file: "+fileName);
                throw ex;
            }
        }
    }

    private void matchEnzymes(List<MsEnzyme> uploadedEnzymes,
            List<MsEnzymeIn> enzymes, String fileName) throws UploadException {
        
        if(uploadedEnzymes.size() != enzymes.size()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage("Number of uploaded enzymes : "+uploadedEnzymes.size()+
                    " does not match # enzymes in file "+fileName+": "+enzymes.size());
            throw ex;
        }
        
        if(uploadedEnzymes.size() == 0)
            return;
        
        Collections.sort(uploadedEnzymes, new Comparator<MsEnzyme>() {
            public int compare(MsEnzyme o1, MsEnzyme o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        Collections.sort(enzymes, new Comparator<MsEnzymeIn>() {
            public int compare(MsEnzymeIn o1, MsEnzymeIn o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        for(int i = 0; i < uploadedEnzymes.size(); i++) {
            if(!matchEnzyme(uploadedEnzymes.get(i), enzymes.get(i))) {
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage("Mismatch in uploaded enzyme and enzyme in file: "+fileName);
                throw ex;
            }
        }
    }
    
    private boolean matchEnzyme(MsEnzyme enzyme1, MsEnzymeIn enzyme2) {
        if(!enzyme1.getName().equals(enzyme2.getName()))
            return false;
        if(!enzyme1.getCut().equals(enzyme2.getCut()))
            return false;
        if(!enzyme1.getNocut().equals(enzyme2.getNocut()))
            return false;
        if(enzyme1.getSense() != enzyme2.getSense())
            return false;
        return true;
        
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
            PepXmlMascotFileReader parser) throws UploadException {
        
        int runSearchId = uploadRunSearchHeader(searchId, runId, parser);
        
        // If the refresh parser has not been run we will initialize the PeptideProteinMatchingService
        if(!parser.isRefreshParserRun()) {
            initializePeptideProteinMatchingService(searchId);
        }
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                PepXmlMascotSearchScanIn scan = parser.getNextSearchScan();
                
                int scanId = getScanId(runId, scan.getScanNumber());
                
                for(MascotPeptideProphetResultIn result: scan.getScanResults()) {
                    // If the refresh parser has not been run, find alternative matches for the peptide
                    if(!parser.isRefreshParserRun()) {
                        
                        MascotSearchResultIn sres = result.getSearchResult();
                        String peptideSeq = sres.getResultPeptide().getPeptideSequence();
                        List<PeptideProteinMatch> matches = proteinMatches.get(peptideSeq);
                        if(matches == null) {
                            matches = matchService.getMatchingProteins(peptideSeq);
                            proteinMatches.put(peptideSeq, matches);
                        }
                        
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
                    uploadMascotResultData(result.getSearchResult().getMascotResultData(), resultId); // Mascot scores
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
        else if (search.getSearchProgram() == Program.MASCOT) {
            MascotSearchDAO mascotDao = DAOFactory.instance().getMascotSearchDAO();
            numEnzymaticTermini = mascotDao.getNumEnzymaticTermini(searchId);
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
        if (mascotResultDataList.size() > 0) {
            uploadMascotResultBuffer();
        }
    }

    // -------------------------------------------------------------------------------------------
    // UPLOAD MASCOT SCORES
    // -------------------------------------------------------------------------------------------
    private void uploadMascotResultData(MascotResultData resultData, int resultId) {
        // upload the Mascot specific result information if the cache has enough entries
        if (mascotResultDataList.size() >= BUF_SIZE) {
            uploadMascotResultBuffer();
        }
        // add the Mascot specific information for this result to the cache
        MascotResultDataWrap resultDataDb = new MascotResultDataWrap(resultData, resultId);
        mascotResultDataList.add(resultDataDb);
    }
    
    private void uploadMascotResultBuffer() {
        mascotResultDao.saveAllMascotResultData(mascotResultDataList);
        mascotResultDataList.clear();
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
            PepXmlMascotFileReader parser) throws UploadException {
        
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

    private int uploadSearch(int experimentId, PepXmlMascotFileReader parser, String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        MascotSearchIn search = parser.getSearch();
        db = search.getSearchDatabases().get(0);
        dynaResidueMods = search.getDynamicResidueMods();
        dynaTermMods = search.getDynamicTerminalMods();
        
        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(db);
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            MascotSearchUploadDAO searchDAO = UploadDAOFactory.getInstance().getMascotSearchDAO();
            return searchDAO.saveSearch(search, experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
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
    
    void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesUploaded = 0;

        resetCaches();

        searchId = 0;
        sequenceDatabaseId = 0;
        
        preUploadCheckMsg = new StringBuilder();
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
    }

    // called before uploading each msms_run_search in the interact.pep.xml file and in the reset() method.
    private void resetCaches() {
        
        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
        mascotResultDataList.clear();
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
        PepXmlMascotDataUploadService p = new PepXmlMascotDataUploadService();
        
        List<String> spectrumFileNames = new ArrayList<String>();
        spectrumFileNames.add("090715_EPO-iT_80mM_HCD.pep.xml");
        p.setSpectrumFileNames(spectrumFileNames);
        
        p.setDirectory("/Users/silmaril/WORK/UW/FLINT/mascot_test");
        p.setSearchDate(new Date());
        p.setExperimentId(37);
        p.upload();
    }
}
