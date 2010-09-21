/**
 * PercolatorXmlDataUploadService.java
 * @author Vagisha Sharma
 * Sep 11, 2010
 */
package org.yeastrc.ms.service.percolator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorPeptideResultBean;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.percolator.PercolatorXmlFileReader;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPeptideResult;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPsmId;
import org.yeastrc.ms.parser.percolator.PercolatorXmlResult;
import org.yeastrc.ms.service.AnalysisDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;

/**
 * 
 */
public class PercolatorXmlDataUploadService implements
		AnalysisDataUploadService {


	private static final Logger log = Logger.getLogger(PercolatorSQTDataUploadService.class);

	public static final String PERC_XML = "combined-results.xml";
	
    private final MsSearchResultDAO resultDao;
    private final MsSearchAnalysisDAO analysisDao;
    private final MsRunSearchDAO runSearchDao;
    private final PercolatorParamsDAO paramDao;
    private final MsSearchDAO searchDao;
    private final MsRunSearchAnalysisDAO runSearchAnalysisDao;
    private final PercolatorResultDAO percResultDao;
    private final PercolatorPeptideResultDAO peptResultDao;

    private static final int BUF_SIZE = 1000;

    private List<? extends MsResidueModificationIn> dynaResidueMods;
    private List<? extends MsTerminalModificationIn> dynaTermMods;
    
    // these are the things we will cache and do bulk-inserts
    private List<PercolatorResultDataWId> percolatorResultDataList; // percolator scores (PSM - level)
    private List<PercolatorPeptideResult> percolatorPeptideResultList; // percolator peptide-level scores
    
    private Set<Integer> uploadedResultIds;

    private int numAnalysisUploaded = 0;
    
    private int analysisId;
    private Map<String,Integer> runSearchIdMap; // key = filename; value = runSearchId
    private Map<String, Integer> runSearchAnalysisIdMap; // key = filename; value runSearchAnalysisId
    Map<Integer, Integer> runIdMap = new HashMap<Integer, Integer>(); // key = runSearchId; value = runId
    
    private int searchId;
    private String dataDirectory;
    private StringBuilder preUploadCheckMsg;
    private boolean preUploadCheckDone = false;
    
//    private List<String> filenames;
    private List<String> searchDataFileNames;
    private Program searchProgram;

	private int numPsmUploaded;
	private int numPeptUploaded;
    
    
    public PercolatorXmlDataUploadService() {
    	
        this.percolatorResultDataList = new ArrayList<PercolatorResultDataWId>(BUF_SIZE);
        this.percolatorPeptideResultList = new ArrayList<PercolatorPeptideResult>(BUF_SIZE);
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        uploadedResultIds = new HashSet<Integer>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.analysisDao = daoFactory.getMsSearchAnalysisDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.paramDao  = daoFactory.getPercoltorParamsDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchAnalysisDao  = daoFactory.getMsRunSearchAnalysisDAO();
        
        this.resultDao = daoFactory.getMsSearchResultDAO();
        this.percResultDao = daoFactory.getPercolatorResultDAO();
        this.peptResultDao = daoFactory.getPercolatorPeptideResultDAO();
        
    }
    
    void reset() {

        analysisId = 0;
        if(runSearchIdMap != null)
        	runSearchIdMap.clear();
        
        numAnalysisUploaded = 0;
        numPsmUploaded = 0;
        numPeptUploaded = 0;

        resetCaches();

        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    void resetCaches() {
        percolatorResultDataList.clear();
        percolatorPeptideResultList.clear();
        uploadedResultIds.clear();
    }


    @Override
    public void upload() throws UploadException {

        reset();// reset all caches etc.
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        
        // now upload results in the Precolator Xml file
        String filePath = dataDirectory+File.separator+PERC_XML;
        
        // Open the XML file and read the Percolator version, params etc.
        PercolatorXmlFileReader reader = new PercolatorXmlFileReader();
        reader.setSearchProgram(Program.SEQUEST);
        reader.setDynamicResidueMods(this.dynaResidueMods);
        try {
        	reader.open(filePath);
        }
        catch(DataProviderException e) {
        	reader.close();
            UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
            ex.setFile(PERC_XML);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        // create a new entry in the msSearchAnalysis table
        try {
            analysisId = saveTopLevelAnalysis(reader.getPercolatorVersion());
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            log.error(e.getMessage(), e);
            reader.close();
            throw e;
        }
        
        
        // Add the Percolator parameters
        try {
            addPercolatorParams(reader.getPercolatorParams(), analysisId);
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
            log.error(e.getMessage(), e);
            deleteAnalysis(analysisId);
            numAnalysisUploaded = 0;
            reader.close();
            throw e;
        }
        
        
        try {
        	numAnalysisUploaded = uploadXml(reader);
        }
        catch (UploadException ex) {
        	ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        	deleteAnalysis(analysisId);
        	numAnalysisUploaded = 0;
        	reader.close();
        	throw ex;
        }
        finally {
        	reader.close();
        }
        
        // if no analyses were uploaded delete the top level search analysis
        if (numAnalysisUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_PERC_ANALYSIS_UPLOADED);
            ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
            deleteAnalysis(analysisId);
            numAnalysisUploaded = 0;
            reader.close();
            throw ex;
        }
        
        
        reader.close();
    }
    
    public List<Integer> getUploadedAnalysisIds() {
        List<Integer> analysisIds = new ArrayList<Integer>();
        analysisIds.add(analysisId);
        return analysisIds;
    }
    
    
    private Map<String, Integer> createRunSearchIdMap() throws UploadException {
        
        Map<String, Integer> runSearchIdMap = new HashMap<String, Integer>(this.searchDataFileNames.size()*2);
        
        for(String file: searchDataFileNames) {
            String filenoext = removeFileExtension(file);
            int runSearchId = runSearchDao.loadIdForSearchAndFileName(searchId, filenoext);
            if(runSearchId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
                ex.appendErrorMessage("File: "+filenoext);
                ex.appendErrorMessage("; SearchID: "+searchId);
                throw ex;
            }
            runSearchIdMap.put(file, runSearchId);
        }
        return runSearchIdMap;
    }


    private void addPercolatorParams(List<PercolatorParam> params, int analysisId) throws UploadException {
        
        for (PercolatorParam param: params) {
            try {
                paramDao.saveParam(param, analysisId);
            }
            catch(RuntimeException e) {
                UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
                ex.appendErrorMessage("Exception saving Percolator param (name: "+param.getParamName()+", value: "+param.getParamValue()+")");
                throw ex;
            }
        }
    }

    private void getSearchModifications(int searchId) {
       MsSearch search = searchDao.loadSearch(searchId);
       this.dynaResidueMods = search.getDynamicResidueMods();
       this.dynaTermMods = search.getDynamicTerminalMods();
    }

    private int saveTopLevelAnalysis(String version) throws UploadException {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
//        analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setAnalysisProgramVersion(version);
        try {
            return analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private int uploadXml(PercolatorXmlFileReader reader) throws UploadException {
        
        log.info("BEGIN PERCOLATOR XML FILE UPLOAD");
        
        long startTime = System.currentTimeMillis();
        
        try {
           runSearchIdMap = createRunSearchIdMap();
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        // read the PSMs
        int numAnalysesUploaded = uploadPsms(reader);
        
        // Now read peptide-level results, if any
    	uploadPeptideResults(reader);
        
        long endTime = System.currentTimeMillis();
        
        log.info("END PERCOLATOR XML FILE UPLOAD; SEARCH_ANALYSIS_ID: "+analysisId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
        return numAnalysesUploaded;
    }

    private int uploadPsms(PercolatorXmlFileReader reader) throws UploadException {
		
		runSearchAnalysisIdMap = new HashMap<String, Integer>(); // key = filename; value runSearchAnalysisId
        runIdMap = new HashMap<Integer, Integer>(); // key = runSearchId; value = runId
        
        
		try {
        	// First read the PSMs
        	while(reader.hasNextPsm()) {

        		PercolatorXmlResult result = (PercolatorXmlResult) reader.getNextPsm();
        		String sourceFileName = result.getFileName();
        		Integer runSearchId = runSearchIdMap.get(sourceFileName);

        		if (runSearchId == null) {
        			UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
        			ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        			deleteAnalysis(analysisId);
        			numAnalysisUploaded = 0;
        			throw ex;
        		}

        		Integer runSearchAnalysisId = runSearchAnalysisIdMap.get(result.getFileName());
        		if(runSearchAnalysisId == null) {
        			runSearchAnalysisId = uploadRunSearchAnalysis(analysisId, runSearchId);
        			log.info("Created new msRunSearchAnalysis entry for file: "+result.getFileName()+"; runSearchID: "+runSearchId+
        					" (runSearchAnalysisId: "+runSearchAnalysisId+")");
        			runSearchAnalysisIdMap.put(result.getFileName(), runSearchAnalysisId);
        		}

        		// get the runID. Will be needed to get the scan ID
                Integer runId = runIdMap.get(runSearchId);
                if(runId == null) {
                	runId = getRunIdForRunSearch(runSearchId);
                	if(runId == 0) {
                		UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
                        ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
                        throw ex;
                	}
                	runIdMap.put(runSearchId, runId);
                }
                
        		int runSearchResultId = getMatchingSearchResultId(runSearchId, result.getScanNumber(), 
        				result.getCharge(), result.getResultPeptide());
        		// upload the Percolator specific information for this result.
                uploadPercolatorResultData(result, runSearchAnalysisId, runSearchResultId);

        	}
        	flushPsmBuffer(); // save any remaining cached data
        	log.info("Uploaded PSMs");
        	
        }
        catch(DataProviderException e) {
        	UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
        	UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        
        return runSearchAnalysisIdMap.size();
	}
    
	private void uploadPeptideResults(PercolatorXmlFileReader reader)
			throws UploadException {
		
		
		try {
			while(reader.hasNextPeptide()) {
				PercolatorXmlPeptideResult peptide = (PercolatorXmlPeptideResult) reader.getNextPeptide();
				
				PercolatorPeptideResultBean peptRes = new PercolatorPeptideResultBean();
				peptRes.setSearchAnalysisId(analysisId);
				peptRes.setResultPeptide(peptide.getResultPeptide());
				peptRes.setQvalue(peptide.getQvalue());
				peptRes.setPosteriorErrorProbability(peptide.getPosteriorErrorProbability());
				peptRes.setPvalue(peptide.getPvalue());
				peptRes.setDiscriminantScore(peptide.getDiscriminantScore());
				List<Integer> psmIds = new ArrayList<Integer>(peptide.getPsmIds().size());
				
				for(PercolatorXmlPsmId psmIdO: peptide.getPsmIds()) {
					// get a PercolatorResult ID for this psm;
					String sourceFileName = psmIdO.getFileName();
	        		Integer runSearchId = runSearchIdMap.get(sourceFileName);

	        		if (runSearchId == null) {
	        			UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
	        			ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
	        			deleteAnalysis(analysisId);
	        			numAnalysisUploaded = 0;
	        			throw ex;
	        		}
					int runSearchResultId = this.getMatchingSearchResultId(runSearchId, psmIdO.getScanNumber(), 
							psmIdO.getCharge(), peptide.getResultPeptide());
					
					// get the PercolatorResult ID for this runSearchResultId;
					Integer runSearchAnalysisId = runSearchAnalysisIdMap.get(sourceFileName);
					if(runSearchAnalysisId == null) {
						UploadException ex = new UploadException(ERROR_CODE.NO_RSANALYSISID_FOR_ANALYSIS_FILE);
	        			ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
	        			deleteAnalysis(analysisId);
	        			numAnalysisUploaded = 0;
	        			throw ex;
					}
					
					PercolatorResult percolatorResult = percResultDao.loadForRunSearchAnalysis(runSearchResultId, runSearchAnalysisId);
					psmIds.add(percolatorResult.getPercolatorResultId());
				}
				peptRes.setPsmIdList(psmIds);
				uploadPercolatorPeptideResult(peptRes);
			}
			
		} catch (DataProviderException e) {
			UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
        	UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        
        flushPeptideBuffer();
        log.info("Uploaded peptides");
	}

	
    // get a matching runSearchResultId
    private int getMatchingSearchResultId(int runSearchId, int scanNumber, int charge, MsSearchResultPeptide peptide) 
    	throws UploadException {
        
    	Integer runId = runIdMap.get(runSearchId);
    	if(runId == null) { // by now we should already have a matching runID, but just in case
    		UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
            ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
            throw ex;
    	}
    	
        int scanId = getScanId(runId, scanNumber);
           
        try {
     	List<Integer> resForScanIds = resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
        	
            List<MsSearchResult> matchingResults = new ArrayList<MsSearchResult>(resForScanIds.size());
            
            for(int resultId: resForScanIds) {
            	MsSearchResult res = resultDao.load(resultId);
            	if(res.getResultPeptide().getPeptideSequence().equals(peptide.getPeptideSequence()))
            		matchingResults.add(res);
            }
//        	List<MsSearchResult> matchingResults = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
//                        charge, 
//                        peptide.getPeptideSequence());
            
            
            if(matchingResults.size() == 1) {
                return matchingResults.get(0).getId();
            }
            
            // no matches were found
            else if(matchingResults.size() == 0) {
            	UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
                ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                		" scanId: "+scanId+"; charge: "+charge+// "; mass: "+result.getObservedMass()+
                		"; peptide: "+peptide.getPeptideSequence()+
                		"; modified peptide: "+peptide.getModifiedPeptidePS());
                throw ex;
                //log.warn(ex.getErrorMessage());
            }
            
            
            else {
            	// If we have multiple matches
            	UploadException ex = new UploadException(ERROR_CODE.MULTI_MATCHING_SEARCH_RESULT);
            	ex.setErrorMessage("Multiple matching search results were found for runSearchId: "+runSearchId+
            			" scanId: "+scanId+"; charge: "+charge+// "; mass: "+result.getObservedMass()+
                		"; peptide: "+peptide.getPeptideSequence()+
                		"; modified peptide: "+peptide.getModifiedPeptidePS());
            	throw ex;
            }
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    

    // Create an entry in the msRunSearchAnalysis table
    private final int uploadRunSearchAnalysis(int analysisId, int runSearchId)
        throws DataProviderException {

        // save the run search analysis and return the database id
        RunSearchAnalysisBean rsa = new RunSearchAnalysisBean();
        rsa.setAnalysisFileFormat(SearchFileFormat.XML_PERC);
        rsa.setAnalysisId(analysisId);
        rsa.setRunSearchId(runSearchId);
        return runSearchAnalysisDao.save(rsa);
    }

    private boolean uploadPercolatorResultData(PercolatorResultIn resultData, int rsAnalysisId, int searchResultId) {
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorResultDataList.size() >= BUF_SIZE) {
            uploadPercolatorResultBuffer();
        }
        
        // TODO THIS IS TEMP TILL I SORT OUT THE DUPLICATE RESULTS IN PERCOLATOR SQT FILES
        if(uploadedResultIds.contains(searchResultId)) {
        	log.warn("MULTIPLE RESULTS FOR searchResultId: "+searchResultId);
            return false;
        }
        uploadedResultIds.add(searchResultId);
        
        
        // add the Percolator specific information for this result to the cache
        PercolatorResultDataBean res = new PercolatorResultDataBean();
        res.setRunSearchAnalysisId(rsAnalysisId);
        res.setSearchResultId(searchResultId);
        res.setPredictedRetentionTime(resultData.getPredictedRetentionTime());
        res.setDiscriminantScore(resultData.getDiscriminantScore());
        res.setPosteriorErrorProbability(resultData.getPosteriorErrorProbability());
        res.setQvalue(resultData.getQvalue());
        res.setPvalue(resultData.getPvalue());
        
       
        percolatorResultDataList.add(res);
        return true;
    }
    
    private boolean uploadPercolatorPeptideResult(PercolatorPeptideResult peptideResult)  {
    	
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorPeptideResultList.size() >= BUF_SIZE) {
            uploadPercolatorPeptideResultBuffer();
        }
        
        percolatorPeptideResultList.add(peptideResult);
        return true;
    }
    
    private void uploadPercolatorResultBuffer() {
        percResultDao.saveAllPercolatorResultData(percolatorResultDataList);
    	numPsmUploaded += percolatorResultDataList.size();
    	if(numPsmUploaded % 10000 == 0)
    		log.info(numPsmUploaded+"  psms uploaded...");
        percolatorResultDataList.clear();
    }
    
    private void flushPsmBuffer() {
        if (percolatorResultDataList.size() > 0) {
            uploadPercolatorResultBuffer();
        }
    }
    
    private void uploadPercolatorPeptideResultBuffer() {
        peptResultDao.saveAllPercolatorPeptideResults(percolatorPeptideResultList);
    	numPeptUploaded += percolatorPeptideResultList.size();
    	if(numPeptUploaded % 10000 == 0)
    		log.info(numPeptUploaded+"  peptides uploaded...");
        percolatorPeptideResultList.clear();
    }
    
    private void flushPeptideBuffer() {
        if (percolatorPeptideResultList.size() > 0) {
            uploadPercolatorPeptideResultBuffer();
        }
    }

    private int getScanId(int runId, int scanNumber) throws UploadException {

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    public void deleteAnalysis(int analysisId) {
        if (analysisId == 0)
            return;
        log.info("Deleting analysis ID: "+analysisId);
        analysisDao.delete(analysisId);
    }
    

    @Override
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
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
        
        // 2. We should have a combined-results.xml file
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".xml");
            }});
        boolean found = false;
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().equals(PERC_XML)) {
            	found = true;
            	break;
            }
        }
        if(!found) {
        	appendToMsg("Could not find file: "+PERC_XML+" in directory: "+dataDirectory);
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
    public String getUploadSummary() {
        return "\tAnalysis file format: "+getAnalysisFileFormat()+
        "\n\t# results uploaded: "+numAnalysisUploaded;
    }

    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return SearchFileFormat.XML_PERC;
    }
    
    @Override
    public void setSearchProgram(Program searchProgram) {
        this.searchProgram = searchProgram;
    }
    
    @Override
    public void setSearchDataFileNames(List<String> searchDataFileNames) {
        this.searchDataFileNames = new ArrayList<String>(searchDataFileNames.size());
        for(String fileName: searchDataFileNames) {
        	int idx = fileName.lastIndexOf(".");
        	if(idx != -1) {
        		fileName = fileName.substring(0,idx);
        	}
        	this.searchDataFileNames.add(fileName);
        }
    }

    public int getMaxPsmRank() {

    	
            String filePath = dataDirectory+File.separator+PERC_XML;
            
            PercolatorXmlFileReader provider = new PercolatorXmlFileReader();
            try {
                provider.open(filePath);
            }
            catch (DataProviderException e) {
                provider.close();
                log.error("Error opening PercolatorXmlFileReader", e);
                return Integer.MAX_VALUE;
            }
            finally { provider.close(); }
            
            List<PercolatorParam> params = provider.getPercolatorParams();
            for(PercolatorParam param: params) {
            	if(param.getParamName().equalsIgnoreCase("command_line")) {
            		String cmdline = param.getParamValue();
            		String[] tokens = cmdline.split(",");
                    for(int i = 0; i < tokens.length; i++) {
                        String val = tokens[i];
                        if(val.startsWith("-m")) {
                            int rank = Integer.parseInt(tokens[++i]);
                            return rank;
                        }
                    }
                    // If we are here it means we did not find the -m flag in the percolator command-line
                    // This means percolator will only use the top hit for each scan+charge combination
                    return 1;
            	}
            }
            log.warn("Could not read percolator command-line to determine value of -m argument. "+
            "ALL sequest results will be uploaded.");
            return Integer.MAX_VALUE;
    }

}
