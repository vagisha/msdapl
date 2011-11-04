package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.general.MsEnzyme;
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
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlBaseFileReader;
import org.yeastrc.ms.parser.pepxml.PepXmlGenericFileReader;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.SearchDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatch;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatchingService;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatchingServiceException;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;

public abstract class PepXmlDataUploadService <T extends PepXmlSearchScanIn<G, R>,
                                               G extends GenericPeptideProphetResultIn<R>, 
                                               R extends MsSearchResultIn,
                                               S extends MsSearchIn>

    implements SearchDataUploadService {

    protected static final int BUF_SIZE = 500;
    private int experimentId;
    private int searchId;

    protected String dataDirectory;
    private Date searchDate;
    private String remoteServer;
    private String remoteDirectory;

    protected StringBuilder preUploadCheckMsg;
    protected boolean preUploadCheckDone;

    protected List<String> searchDataFileNames; // names without extensions
    private String fileExtension;
    private List<String> spectrumFileNames;

    

    private final MsRunDAO runDao;
    private final MsScanDAO scanDao;
    private final MsSearchDatabaseDAO sequenceDbDao;
    private final MsRunSearchDAO runSearchDao;
    private final MsSearchResultProteinDAO proteinMatchDao;
    private final MsSearchModificationDAO modDao;
    private final MsSearchResultDAO resultDao;
    private final MsSearchDAO searchDao;

    private List<MsSearchResultProtein> proteinMatchList;
    private List<MsResultResidueModIds> resultResidueModList;
    private List<MsResultTerminalModIds> resultTerminalModList;
    private MsSearchDatabaseIn db = null;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;

    private int sequenceDatabaseId;
    private DynamicModLookupUtil dynaModLookup;
    private int numSearchesUploaded = 0;

    // private Map<String, List<PeptideProteinMatch>> proteinMatches;
    private PeptideProteinMatchingService matchService;

    private static final Logger log = Logger.getLogger(PepXmlDataUploadService.class.getName());

    public PepXmlDataUploadService() {

        super();
        this.searchDataFileNames = new ArrayList<String>();

        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);

        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();

        //proteinMatches = new HashMap<String, List<PeptideProteinMatch>>();

        DAOFactory daoFactory = DAOFactory.instance();

        this.runDao = daoFactory.getMsRunDAO(); 
        this.scanDao = daoFactory.getMsScanDAO();
        this.sequenceDbDao = daoFactory.getMsSequenceDatabaseDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
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
        return "\tSearch file format: "+getSearchFileFormat()+
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

        // 2. Look for *.xml or *.pep.xml file that may contain search results
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return (name_uc.endsWith(".pep.xml") || name_uc.endsWith(".xml"));
            }});
        // Remove files where PeptideProphet has been run. We are interested in files
        // that contain only search results.
        List<File> searchResultFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
        	String fileName = files[i].getName();
        	
        	PepXmlBaseFileReader parser = new PepXmlBaseFileReader();
            try {
                parser.open(dataDirectory+File.separator+fileName);
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+fileName+"\n"+e.getMessage());
                return false;
            }
            
            if(!parser.isTPPFile()) {
            	parser.close();
            	continue;
            }
            else if (parser.isPeptideProphetRun()) {
            	parser.close();
            	continue;
            }
            // This is a TPP-generated file and does not contain PeptideProphet results
            searchResultFiles.add(files[i]);
            parser.close();
        }
        // If we did not find any TPP generated files that also do not contain PeptideProphet results
        // add files that contain PeptideProphet results.  In this case we will only upload
        // search results that made it through the PeptideProphet analysis
        if(searchResultFiles.size() == 0) {
        	
        	log.info("No files found containing only search results and not PeptideProphet results");
        	for (int i = 0; i < files.length; i++) {
            	String fileName = files[i].getName();
            	
            	PepXmlBaseFileReader parser = new PepXmlBaseFileReader();
                try {
                    parser.open(dataDirectory+File.separator+fileName);
                }
                catch (DataProviderException e) {
                    appendToMsg("Error opening file: "+fileName+"\n"+e.getMessage());
                    return false;
                }
                
                if(parser.isTPPFile()) {
                	searchResultFiles.add(files[i]);
                }
                parser.close();
            }
        }
        
        for (File file: searchResultFiles) {
            String name = file.getName();
            
            int idx = name.lastIndexOf(".pep.xml");
            if(idx == -1)
            	idx = name.lastIndexOf(".xml");
            
            String ext = name.substring(idx);
            name = name.substring(0, idx);
            searchDataFileNames.add(name);
            
            
            if(this.fileExtension == null) {
            	this.fileExtension = ext;
            }
            else {
            	
            	if(!this.fileExtension.equals(ext)) {
            		appendToMsg("Multiple file extensions: "+this.fileExtension+", "+ext);
            		return false;
            	}
            }
        }
        log.info("File extension is: "+this.fileExtension);


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

    protected void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

    // ----------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    // ----------------------------------------------------------------------------------------------------
    
    protected abstract PepXmlGenericFileReader<T, G, R, S> getPepXmlReader();
    
    protected abstract S getSearchAndParams(String dataDirectory, String remoteServer,
            String remoteDirectory, Date searchDate) throws UploadException;
    
    protected abstract int saveSearch(S search, int experimentId, int sequenceDatabaseId);
    
    protected abstract void uploadProgramSpecificResultData(R result, int resultId);
    
    protected abstract int getNumEnzymaticTermini(int searchId);
    
    protected abstract boolean getClipNtermMethionine(int searchId);
    
    protected abstract SearchFileFormat getSearchFileFormat();
    
    // ----------------------------------------------------------------------------------------------------


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
        // parse and upload the search parameters
        try {
            S search = getSearchAndParams(dataDirectory, remoteServer, remoteDirectory, searchDate);
            searchId = this.uploadSearch(experimentId, search);
            
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
            throw e;
        }

        // initialize the Modification lookup map; will be used when uploading modifications for search results
        dynaModLookup = new DynamicModLookupUtil(searchId);
        
        // now upload the search data in the *.pep.xml or *.xml files
        for (String file: searchDataFileNames) {
            
            Integer runId = runIdMap.get(file); 

            String filePath = dataDirectory+File.separator+file+fileExtension;
            log.info("Reading file: "+filePath);
            
            resetCaches();
            // int runSearchId;

            long s = System.currentTimeMillis();
            log.info("Uploading search results in file: "+file);
            PepXmlGenericFileReader<T,G,R,S> parser = getPepXmlReader();
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

                    // match the search parameters found in the file against the ones we saved
                    // for this search
                    matchSearchParams(searchId, parser.getSearch(), parser.getRunSearchName());

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

    protected void matchSearchParams(int searchId, MsSearchIn parsedSearch, String fileName)
    throws UploadException {

        // load the search and its parameters, enzyme information, database information
        // and modification information
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);

        SearchParamMatcher matcher = new SearchParamMatcher();
        boolean matches = matcher.matchSearchParams(search, parsedSearch, fileName);

        if(!matches) {
            log.error(matcher.getErrorMessage());
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage(matcher.getErrorMessage());
            throw ex;
        }
        // TODO do we need to match some other key parameters e.g. min_enzymatic_termini etc. 
    }

    private int getScanId(int runId, int scanNumber) throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }

    private void uploadRunSearch(String filename, int searchId, int runId, PepXmlGenericFileReader<T,G,R,S> parser) 
        throws UploadException {

    	log.info("Loading search results for file: "+filename);
    	
        int runSearchId = uploadRunSearchHeader(searchId, runId, parser);
        log.info("Created entry in msRunSearch table: "+runSearchId);
        
        Map<String, List<PeptideProteinMatch>> proteinMatches = new HashMap<String, List<PeptideProteinMatch>>();
        
        // If the refresh parser has not been run we will initialize the PeptideProteinMatchingService
        if(!parser.isRefreshParserRun()) {
            initializePeptideProteinMatchingService(searchId);
        }

        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                T scan = parser.getNextSearchScan();

                int scanId = getScanId(runId, scan.getScanNumber());

                for(G result: scan.getScanResults()) {
                    // If the refresh parser has not been run, find alternative matches for the peptide
                    if(!parser.isRefreshParserRun()) {

                        R sres = result.getSearchResult();
                        String peptideSeq = sres.getResultPeptide().getPeptideSequence();
                        List<PeptideProteinMatch> matches = proteinMatches.get(peptideSeq);
                        if(matches == null) {
                            try {
								matches = matchService.getMatchingProteins(peptideSeq);
							} catch (PeptideProteinMatchingServiceException e) {
								log.error("Error finding protein matches. ", e); // we are not propagating this up because the cause of the  
																				 // exception may be that no matches were found. We will give it
																				 // one more shot. 
							}
                            if(matches == null || matches.size() == 0) {

                                // This can happen if no matching protein was found with the search 
                                // constraints used. Mascot still reports the hit. 
                                // So we relax the constraints and search again
                                int oldNet = matchService.getNumEnzymaticTermini();
                                if(oldNet > 0) {
                                    log.info("No protein match found for peptide: "+peptideSeq+"; relaxing NET and searching again...");
                                    matchService.setNumEnzymaticTermini(0);
                                    
                                    try {
										matches = matchService.getMatchingProteins(peptideSeq);
									} catch (PeptideProteinMatchingServiceException e) {
										log.error("Error finding protein matches. ", e);
										 UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
										 ex.setErrorMessage("No protein matches found for peptide: "+peptideSeq);
										 ex.appendErrorMessage(e.getMessage());
										 throw ex;
									}
                                    
                                    matchService.setNumEnzymaticTermini(oldNet); // set it back
                                }

                                // If we still did not find any matches
                                if(matches == null || matches.size() == 0) {
                                    UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                                    ex.setErrorMessage("No protein matches found for peptide: "+peptideSeq);
                                    throw ex;
                                }
                            }
                            proteinMatches.put(peptideSeq, matches);
                        }

                        List<MsSearchResultProteinIn> protList = sres.getProteinMatchList();
                        // truncate accessions if required
                        // accessions in the pepXML files can be longer than the accessions in YRC_NRSEQ
                        // Limit was 255 now increased to 500
                        // If the fasta file has already been uploaded it means that 
                        // 500 chars are enough to uniquely identify this accession in the fasta file.
                        for(MsSearchResultProteinIn prot: protList) {
                        	if(prot.getAccession().length() > 500) {
                        		log.warn("Truncating LONG Protein name in pepXML; length: "+prot.getAccession().length()+"\n"+
                        				prot.getAccession());
                        		prot.setAccession(prot.getAccession().substring(0,500));
                        	}
                        }

                        List<DbLocus> newLoci = new ArrayList<DbLocus>();
                        
                        for(PeptideProteinMatch match: matches) {
                        	
                            boolean haveAlready = false;
                            for(MsSearchResultProteinIn prot: protList) {
                                if(match.getProtein().getAccessionString().equals(prot.getAccession())) { // this one we have already
                                    haveAlready = true;
                                    break;
                                }
                                // It is possible that our protein was entered into the database BEFORE column definition
                                // was changed to 500 chars
                                // In this case we look for a matching prefix
                                else if(match.getProtein().getAccessionString().length() == 255) {
                                	
                                	if(prot.getAccession().startsWith(match.getProtein().getAccessionString())) {
                                		// update the accession so that we don't have trouble matching it with 
                                		// entries in YRC_NRSEQ later
                                		prot.setAccession(match.getProtein().getAccessionString());
                                		haveAlready = true;
                                		break;
                                	}
                                }
                                // What if the protein names in pepXML are truncated.
//                                else if(match.getProtein().getAccessionString().startsWith(prot.getAccession())) {
//                                	// update the accession so that we don't have trouble matching it with 
//                            		// entries in YRC_NRSEQ later
//                            		prot.setAccession(match.getProtein().getAccessionString());
//                            		haveAlready = true;
//                            		break;
//                                }
                            }
                            
                            if(haveAlready)
                                continue;
                            DbLocus locus = new DbLocus(match.getProtein().getAccessionString(), match.getProtein().getDescription());
                            locus.setNtermResidue(match.getPreResidue());
                            locus.setCtermResidue(match.getPostResidue());
                            locus.setNumEnzymaticTermini(match.getNumEnzymaticTermini());
                            newLoci.add(locus);
                        }
                        
                        for(DbLocus locus: newLoci) {
                        	sres.addMatchingProteinMatch(locus);
                        }
                    }
                    
                    int resultId = uploadBaseSearchResult(result.getSearchResult(), runSearchId, scanId);
                    uploadProgramSpecificResultData(result.getSearchResult(), resultId); // Program specific scores
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

    private void initializePeptideProteinMatchingService(int searchId)
    throws UploadException {

        if(this.matchService != null)
            return;

        // get the search
        MsSearch search = searchDao.loadSearch(searchId);
        List<MsEnzyme> enzymes = search.getEnzymeList();
        List<MsSearchDatabase> databases = search.getSearchDatabases();

        
        int numEnzymaticTermini = 0; 
        
        
        // Sequest pepXML files have all protein matches regardless of NET
        if(search.getSearchProgram() == Program.SEQUEST)
        	numEnzymaticTermini = 0;
        else if(search.getSearchProgram() == Program.MASCOT)
        	numEnzymaticTermini = getNumEnzymaticTermini(searchId);

        boolean clipNterMet = getClipNtermMethionine(searchId);
        
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
        catch (PeptideProteinMatchingServiceException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.setErrorMessage("Error initializing PeptideProteinMatchingService for databaseID: "+
                    databases.get(0).getSequenceDatabaseId());
            ex.appendErrorMessage(e.getMessage());
            throw ex; 
        }

        matchService.setNumEnzymaticTermini(numEnzymaticTermini);
        matchService.setEnzymes(enzymes);
        matchService.setDoItoLSubstitution(false);
        matchService.setClipNtermMet(clipNterMet);
        matchService.setRemoveAsterisks(false); // '*' in a protein sequence will be treated as protein ends.
    }

    protected void flush() {
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

    private int uploadBaseSearchResult(MsSearchResultIn result, int runSearchId, int scanId)
            throws UploadException {

        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY

        // upload the protein matches
        uploadProteinMatches(result, resultId, sequenceDatabaseId);

        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);

        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);

        return resultId;
    }

//    private <T extends MsSearchResult> List<Integer> uploadBaseSearchResults(
//            List<T> results) throws UploadException {
//
//        List<Integer> autoIncrIds = resultDao.saveResultsOnly(results);
//        for(int i = 0; i < results.size(); i++) {
//            MsSearchResult result = results.get(i);
//            int resultId = autoIncrIds.get(i);
//
//            // upload the protein matches
//            uploadProteinMatches(result, resultId);
//
//            // upload dynamic mods for this result
//            uploadResultResidueMods(result, resultId, result.getRunSearchId());
//
//            // no dynamic terminal mods for sequest
//            uploadResultTerminalMods(result, resultId, searchId);
//        }
//
//        return autoIncrIds;
//    }

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
            log.debug("Adding match: resultID: "+resultId+"; Accession : "+match.getAccession());
            accSet.add(match.getAccession());
            proteinMatchList.add(new SearchResultProteinBean(resultId, match.getAccession()));
        }
    }

//    private final void uploadProteinMatches(MsSearchResult result, final int resultId)
//    throws UploadException {
//        // upload the protein matches if the cache has enough entries
//        if (proteinMatchList.size() >= BUF_SIZE) {
//            uploadProteinMatchBuffer();
//        }
//        // add the protein matches for this result to the cache
//        Set<String> accSet = new HashSet<String>(result.getProteinMatchList().size());
//        for (MsSearchResultProtein match: result.getProteinMatchList()) {
//            // only UNIQUE accession strings for this result will be added.
//            if (accSet.contains(match.getAccession()))
//                continue;
//            accSet.add(match.getAccession());
//            proteinMatchList.add(new SearchResultProteinBean(result.getId(), match.getAccession()));
//        }
//    }

    private void uploadProteinMatchBuffer() {

        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        proteinMatchDao.saveAll(list);
        proteinMatchList.clear();
    }

    private void uploadResultResidueMods(MsSearchResultIn result, int resultId,
            int searchId) throws UploadException {
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
                        "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                        "; position: "+mod.getModifiedPosition()+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modMatch.getId(), mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }

//    private void uploadResultResidueMods(MsSearchResult result, int resultId,
//            int searchId) throws UploadException {
//        // upload the result dynamic residue modifications if the cache has enough entries
//        if (resultResidueModList.size() >= BUF_SIZE) {
//            uploadResultResidueModBuffer();
//        }
//        // add the dynamic residue modifications for this result to the cache
//        for (MsResultResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
//            if (mod == null)
//                continue;
//            int modId = dynaModLookup.getDynamicResidueModificationId( 
//                    mod.getModifiedResidue(), mod.getModificationMass()); 
//            if (modId == 0) {
//                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
//                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
//                        searchId+
//                        "; modResidue: "+mod.getModifiedResidue()+
//                        "; modMass: "+mod.getModificationMass().doubleValue());
//                throw ex;
//            }
//            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modId, mod.getModifiedPosition());
//            resultResidueModList.add(resultMod);
//        }
//    }

    private void uploadResultResidueModBuffer() {
        modDao.saveAllDynamicResidueModsForResult(resultResidueModList);
        resultResidueModList.clear();
    }

    void uploadResultTerminalMods(MsSearchResultIn result, int resultId, int searchId)
    throws UploadException {
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

//    private void uploadResultTerminalMods(MsSearchResult result, int resultId,
//            int searchId) throws UploadException {
//        // upload the result dynamic terminal modifications if the cache has enough entries
//        if (resultTerminalModList.size() >= BUF_SIZE) {
//            uploadResultTerminalModBuffer();
//        }
//        // add the dynamic terminal modifications for this result to the cache
//        for (MsTerminalModificationIn mod: result.getResultPeptide().getResultDynamicTerminalModifications()) {
//            if (mod == null)
//                continue;
//            int modId = dynaModLookup.getDynamicTerminalModificationId(
//                    mod.getModifiedTerminal(), mod.getModificationMass()); 
//            if (modId == 0) {
//                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
//                ex.setErrorMessage("No matching dynamic terminal modification found for: searchId: "+
//                        searchId+
//                        "; modTerminal: "+mod.getModifiedTerminal()+
//                        "; modMass: "+mod.getModificationMass().doubleValue());
//                throw ex;
//            }
//            ResultTerminalModIds resultMod = new ResultTerminalModIds(resultId, modId);
//            resultTerminalModList.add(resultMod);
//        }
//    }

    private void uploadResultTerminalModBuffer() {
        modDao.saveAllDynamicTerminalModsForResult(resultTerminalModList);
        resultTerminalModList.clear();
    }

    private int uploadRunSearchHeader(int searchId, int runId, PepXmlGenericFileReader<T,G,R,S> parser)
    throws UploadException {

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

    private int uploadSearch(int experimentId, S search) throws UploadException {

        db = search.getSearchDatabases().get(0);
        dynaResidueMods = search.getDynamicResidueMods();
        dynaTermMods = search.getDynamicTerminalMods();

        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(db);

        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            return saveSearch(search, experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }


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

    protected void reset() {

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

    protected void resetCaches() {

        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
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

}