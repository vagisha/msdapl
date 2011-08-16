/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.FileFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectraDataType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIDFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.jaxb.UserParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType.SourceFile;

/**
 * SequestSqt2MzidWriter.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class SequestMsData2MzidWriter extends SequestMzidWriter {
	
	private DAOFactory daoFactory = DAOFactory.instance();
	private int experimentId;
	private MsSearch search;
	
	
	private String sequestVersion;
	
	// modifications
	// private List<MsResidueModification> dynamicResidueMods;
	private List<MsResidueModification> staticResidueMods;
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	// original location of the search files
	private String originalSearchDirectory;
	
	// files
	private Map<Integer, String> runSearchIdFileNameMap;
	
	// file format (for searches)
	private SearchFileFormat searchFileFormat;
	private String searchFileExtension;
	
	// file format (for spectra)
	private RunFileFormat runFileFormat;
	private String runFileExtension;
	
	
	public void setExperimentId(int experimentId) throws MzIdentMlWriterException {
		
		this.experimentId = experimentId;
	}
	
	void initializeFieldsBeforeWrite() throws MzIdentMlWriterException {
		
		
		if(experimentId <= 0) {
			throw new MzIdentMlWriterException("Invalid experimentId: "+experimentId);
		}
		
		// load the experiment
		MsExperiment experiment = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
		if(experiment == null) {
			throw new MzIdentMlWriterException("Experiment with ID: "+experimentId+" was not found in the database");
		}
		
		// get the search IDs for this experiment; make sure we have only one
		List<Integer> searchIds = daoFactory.getMsSearchDAO().getSearchIdsForExperiment(experimentId);
		if(searchIds.size() == 0) {
			throw new MzIdentMlWriterException("Experiment with ID: "+experimentId+" does not have any searches");
		}
		if(searchIds.size() > 1) {
			throw new MzIdentMlWriterException("Experiment with ID: "+experimentId+" has multiple searches");
		}
		
		int searchId = searchIds.get(0);
		
		// load the search
		this.search = daoFactory.getMsSearchDAO().loadSearch(searchId);
		if(!(Program.isSequest(search.getSearchProgram()))) {
			
			throw new MzIdentMlWriterException("Search with ID: "+searchId+" is not a Sequest search");
		}
		
		this.originalSearchDirectory = search.getServerDirectory();
		
		this.sequestVersion = search.getSearchProgramVersion();
		
		// get the fasta file used for this search; make sure there is only one
		List<MsSearchDatabase> searchDatabases = search.getSearchDatabases();
		if(searchDatabases.size() == 0) {
			
			throw new MzIdentMlWriterException("No fasta databases found for search with ID: "+searchId);
			
		}
		if(searchDatabases.size() > 1) {
			
			throw new MzIdentMlWriterException("Multiple fasta databases found for search with ID: "+searchId);
		}
		
		this.fastaFilePath = searchDatabases.get(0).getServerPath();
		this.fastaFileName = searchDatabases.get(0).getDatabaseFileName();
		
		if(StringUtils.isBlank(this.fastaFileName)) {
    		throw new MzIdentMlWriterException("Could not find fasta database name");
    	}
		
		// get the static modification
		this.staticResidueMods = search.getStaticResidueMods();
		
		// get the filename for the run in this search
		initializeFileNamesAndFileFormats();
		
	}
	
	private void initializeFileNamesAndFileFormats() {
		
		MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
		MsRunDAO runDao = daoFactory.getMsRunDAO();
		
		this.runSearchIdFileNameMap = new HashMap<Integer, String>();
		
		// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
		List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
		for(int runSearchId: runSearchIds) {
			
			String filename = rsDao.loadFilenameForRunSearch(runSearchId);
			
			this.runSearchIdFileNameMap.put(runSearchId, filename);
			
			MsRunSearch runSearch = rsDao.loadRunSearch(runSearchId);
			this.searchFileFormat = runSearch.getSearchFileFormat();
			if(this.searchFileFormat == SearchFileFormat.SQT_SEQ) {
				this.searchFileExtension = ".sqt";
			}
			else if(searchFileFormat == SearchFileFormat.PEPXML_SEQ) {
				this.searchFileExtension = "pep.xml";
			}
			
			MsRun run = runDao.loadRun(runSearch.getRunId());
			this.runFileFormat = run.getRunFileFormat();
			if(this.runFileFormat == RunFileFormat.MS2) {
				this.runFileExtension = ".ms2";
			}
			else if(this.runFileFormat == RunFileFormat.CMS2) {
				this.runFileExtension = ".cms2";
			}
			else if(this.runFileFormat == RunFileFormat.MZXML) {
				this.runFileExtension = ".mzXML";
			}
        }
	}
	
	
	// --------------------------------------------------------------------------------------------
	// Implementation of superclass' abstract methods
	String getSequestVersion() {
		return this.sequestVersion;
	}
	
	String getFastaFilePath() {
		return this.fastaFilePath;
	}
	
	String getFastaFileName() {
		return this.fastaFileName;
	}
	
	void writeDbSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> proteinAccessions = new HashSet<String>();
		
        
		MsSearchResultDAO searchResDao = daoFactory.getMsSearchResultDAO();
		MsSearchResultProteinDAO proteinDao = daoFactory.getMsProteinMatchDAO();
		
        try {
        	
        	// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
        	List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
        	for(int runSearchId: runSearchIds) {
        		
        		// get the resultIds; (one resultId per PSM)
        		List<Integer> resultIds = searchResDao.loadResultIdsForRunSearch(runSearchId);
        		
        		// read the PSMs
        		for(int resultId: resultIds) {

        			List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(resultId);
        			
        			for(MsSearchResultProtein protein: proteins) {

        				String accession = protein.getAccession();

        				if(!proteinAccessions.contains(accession)) {

        					DbSequenceMaker seqMaker = new DbSequenceMaker();
        					seqMaker.setAccession(accession);
        					seqMaker.setId(accession);
        					seqMaker.setSearchDatabase(this.fastaFileName);

        					// TODO lookup YRC_NRSEQ and get the protein description

        					DBSequenceType seqType = seqMaker.make();
        					seqCollWriter.addSequence(seqType);

        					// add to our list
        					proteinAccessions.add(accession);
        				}
        			}
        		}
        	}
        }
        catch(RuntimeException e) {
        	throw new MzIdentMlWriterException("Error getting data from database", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		}
	}

	void writePeptideSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		MsSearchResultDAO searchResDao = daoFactory.getMsSearchResultDAO();
		
        try {
        	
        	// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
        	List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
        	for(int runSearchId: runSearchIds) {
        		
        		// get the resultIds; (one resultId per PSM)
        		List<Integer> resultIds = searchResDao.loadResultIdsForRunSearch(runSearchId);
        		
        		// read the PSMs
        		for(int resultId: resultIds) {
        			
        			MsSearchResult psm = searchResDao.load(resultId);
        			
        			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
        			
        			String modifiedSeq = resultPeptide.getModifiedPeptide();
        			
        			
        			if(peptideSequences.contains(modifiedSeq))
        				continue;
        			
        			PeptideMaker peptideMaker = new PeptideMaker(this.getUnimodRepository());
        			
        			peptideMaker.setId(modifiedSeq);
        			peptideMaker.setSequence(resultPeptide.getPeptideSequence());
        			
        			// look for modifications (DYNAMIC)
        			List<MsResultResidueMod> dynaResMods = resultPeptide.getResultDynamicResidueModifications();
        			
        			for(MsResultResidueMod mod: dynaResMods) {

        				try {
        					peptideMaker.addModification(mod.getModifiedPosition(), mod.getModificationMass().doubleValue());
        				}
        				catch(UnimodRepositoryException e) {
        					throw new MzIdentMlWriterException("Unimod repository lookup failed for dynamic modification at position "+
        							mod.getModifiedPosition()+" with delta mass: "+mod.getModificationMass()+" in peptide: "+resultPeptide.getPeptideSequence(), e);
        				}
        			}
        			
        			
        			// Do we have any static modifications? 
        			for(MsResidueModification smod: staticResidueMods) {
        				
        				String peptideSequence = resultPeptide.getPeptideSequence();
        				int idx = -1;
        				
        				while((idx = peptideSequence.indexOf(smod.getModifiedResidue(), idx+1)) != -1) {
        					
        					try {
								peptideMaker.addModification(idx, smod.getModificationMass().doubleValue());
							} catch (UnimodRepositoryException e) {
								throw new MzIdentMlWriterException("Unimod repository lookup failed for static modification at position "+
	        							idx+" with delta mass: "+smod.getModificationMass()+" in peptide: "+resultPeptide.getPeptideSequence(), e);
							}
        				}
        			}
        			
        			// TODO deal with terminal modifications
        			List<MsResultTerminalMod> dynaTermMods = resultPeptide.getResultDynamicTerminalModifications();
        			
        			PeptideType peptideType = peptideMaker.make();
        			seqCollWriter.addPeptide(peptideType);
        			
        			peptideSequences.add(modifiedSeq);
        		}
        		
        	}
        }
        catch(RuntimeException e) {
        	throw new MzIdentMlWriterException("Error getting data from database", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzIdentMlWriterException("There was an error building modified sequence for a peptide", e);
		}
	}

	void writePeptideEvidences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		MsSearchResultDAO searchResDao = daoFactory.getMsSearchResultDAO();
		MsSearchResultProteinDAO proteinDao = daoFactory.getMsProteinMatchDAO();
		
        try {
        	
        	// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
        	List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
        	for(int runSearchId: runSearchIds) {
        		
        		// get the resultIds; (one resultId per PSM)
        		List<Integer> resultIds = searchResDao.loadResultIdsForRunSearch(runSearchId);
        		
        		// read the PSMs
        		for(int resultId: resultIds) {
        			
        			MsSearchResult psm = searchResDao.load(resultId);
        			
        			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
        			
        			String modifiedSeq = resultPeptide.getModifiedPeptide();
        			
        			
        			if(peptideSequences.contains(modifiedSeq))
        				continue;
        			
        			List<MsSearchResultProtein> proteins = proteinDao.loadResultProteins(resultId);
        			
        			int evidenceCount = 1;
        			
        			for(MsSearchResultProtein protein: proteins) {
        				
        				PeptideEvidenceMaker pevMaker = new PeptideEvidenceMaker();
        				
        				pevMaker.setId(modifiedSeq+"_Ev"+evidenceCount++);
        				pevMaker.setPeptide_ref(modifiedSeq);
        				
        				// we use protein accession as the id for the protein (DBSequence element)
        				pevMaker.setDbSequence_ref(protein.getAccession());
        				
        				pevMaker.setPreResidue(String.valueOf(resultPeptide.getPreResidue()));
        				pevMaker.setPostResidue(String.valueOf(resultPeptide.getPostResidue()));
        				
        				PeptideEvidenceType pevT = pevMaker.make();
        				
        				seqCollWriter.addPeptideEvidence(pevT);
        			}
        			
        			peptideSequences.add(modifiedSeq);
        		}
        		
        	}
        }
        catch(RuntimeException e) {
        	throw new MzIdentMlWriterException("Error getting data from database", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzIdentMlWriterException("There was an error building modified sequence for a peptide", e);
		}
	}

	List<InputSpectraType> getInputSpectraList() {
		
		List<InputSpectraType> list = new ArrayList<InputSpectraType>();
        	
		for(String filename: this.runSearchIdFileNameMap.values()) {
			
			InputSpectraType spectra = new InputSpectraType();
			spectra.setSpectraDataRef(filename+this.runFileExtension);
			
			list.add(spectra);
        }
		
		return list;
	}
	
	
	AnalysisProtocolCollectionType getAnalysisProtocolCollection() throws MzIdentMlWriterException, JAXBException {
		
		AnalysisProtocolCollectionMaker collMaker = new AnalysisProtocolCollectionMaker_FromMsData(this.getUnimodRepository(), this.search);
		
		return collMaker.getSequestAnalysisProtocol();
	}
	
	
	InputsType getInputs() throws MzIdentMlWriterException {
		
		InputsType inputs = new InputsType();
		
		
		// Source files(s)
		for(String filename: this.runSearchIdFileNameMap.values()) {
			SourceFile srcFile = new SourceFile();
			inputs.getSourceFile().add(srcFile);
			
			FileFormatType formatType = new FileFormatType();
			
			srcFile.setId(filename+this.searchFileExtension);
			srcFile.setLocation(this.originalSearchDirectory+File.separator+filename+this.searchFileExtension);
			
			if(this.searchFileFormat == SearchFileFormat.SQT_SEQ) {
				
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001563", "Sequest SQT", CvConstants.PSI_CV));
			}
			else if(this.searchFileFormat == SearchFileFormat.PEPXML_SEQ) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001421", "pepXML file", CvConstants.PSI_CV));
			}
			srcFile.setFileFormat(formatType);
		}
		
		// Database (fasta)
		SearchDatabaseType searchDbType = new SearchDatabaseType();
		inputs.getSearchDatabase().add(searchDbType);
		searchDbType.setId(this.fastaFileName);
		ParamType dbName = new ParamType();
		UserParamType userParam = new UserParamType();
		userParam.setName(this.fastaFileName);
		dbName.setUserParam(userParam);
		searchDbType.setDatabaseName(dbName);
		searchDbType.setLocation(this.fastaFilePath);
		
		// Spectra file (.cms2 file)
		for(String filename: this.runSearchIdFileNameMap.values()) {
			
			SpectraDataType spectraData = new SpectraDataType();
			inputs.getSpectraData().add(spectraData);
			
			SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
			specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
			spectraData.setSpectrumIDFormat(specIdFmt);
			
			FileFormatType formatType = new FileFormatType();
			
			String fullfilename = filename + runFileExtension;
			String spectraFile = this.originalSearchDirectory+File.separator+filename + runFileExtension;
			
			if(runFileFormat == RunFileFormat.MS2) {
				
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
			}
			else if(runFileFormat == RunFileFormat.CMS2) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
			}
			else if(runFileFormat == RunFileFormat.MZXML) {
				formatType.setCvParam(CvParamMaker.getInstance().make("MS:1000566", "ISB mzXML file", CvConstants.PSI_CV));
			}
			else
				throw new MzIdentMlWriterException("Unrecognized spectra file format: "+runFileFormat);
			
			
			spectraData.setId(fullfilename);
			spectraData.setLocation(spectraFile);
			spectraData.setFileFormat(formatType);
		}
		
		return inputs;
	}
	
	void writeSearchResults() throws MzIdentMlWriterException {
		
		
		SequestSearchResultDAO searchResDao = daoFactory.getSequestResultDAO();
		
		ResultSortCriteria sortCriteria = new ResultSortCriteria(SORT_BY.SCAN, SORT_ORDER.ASC);
		
        try {
        	
        	// get the runSearchIds for this search; there is one runSearchId per .sqt file uploaded for this search
        	List<Integer> runSearchIds = daoFactory.getMsRunSearchDAO().loadRunSearchIdsForSearch(this.search.getId());
        	
        	for(int runSearchId: runSearchIds) {
        		
        		// get the resultIds sorted by scan number
        		List<Integer> resultIds = searchResDao.loadResultIdsForRunSearch(runSearchId, null, sortCriteria);
        		
        		int lastScanId = -1;
        		List<SequestSearchResult> sequestResults = new ArrayList<SequestSearchResult>();
        		
        		for(int resultId: resultIds) {
        			// read the PSMs
        		
        			SequestSearchResult result = searchResDao.load(resultId);
        			
        			if(result.getScanId() != lastScanId) {
        				
        				if(lastScanId == -1) {
        					
        					addScansForSpectrum(sequestResults);
        					sequestResults.clear();
        				}
        			}
        			
        			lastScanId = result.getScanId();
        			sequestResults.add(result);
        		}
        			
        		// last one
        		addScansForSpectrum(sequestResults);
        		
        		
        	}
        }
        catch(RuntimeException e) {
        	throw new MzIdentMlWriterException("Error getting data from database", e);
        } catch (ModifiedSequenceBuilderException e) {
        	throw new MzIdentMlWriterException("Error building modified peptide sequence for psm", e);
		} 
	}
	
	
	
	private void addScansForSpectrum(List<SequestSearchResult> sequestResults) throws ModifiedSequenceBuilderException, MzIdentMlWriterException {
		
		if(sequestResults.size() == 0)
			return;
		
		String filename = getFileNameForRunSearchId(sequestResults.get(0).getRunSearchId());
		filename += this.runFileExtension;
		
		int scanNumber = getScanNumber(sequestResults.get(0).getScanId());
		
		SpectrumIdentificationResultMaker specResultMaker = new SpectrumIdentificationResultMaker();
		specResultMaker.initSpectrumResult(filename, scanNumber);
		
		for(SequestSearchResult psm: sequestResults) {
			
			specResultMaker.addSequestResult(psm);
		}
		
		SpectrumIdentificationResultType result = specResultMaker.getSpectrumResult();
		
		super.writeSearchResult(result);
	}

	private int getScanNumber(int scanId) {
		
		MsScan scan = DAOFactory.instance().getMsScanDAO().load(scanId);
		return scan.getStartScanNum();
	}

	private String getFileNameForRunSearchId(int runSearchId) {
		
		return this.runSearchIdFileNameMap.get(runSearchId);
	}

	public static void main(String[] args) throws MzIdentMlWriterException {
		
		SequestMsData2MzidWriter writer = new SequestMsData2MzidWriter();
		
		writer.setExperimentId(105);
		
		writer.setOutputFilePath("/Users/vagisha/WORK/MSDaPl_data/two-ms2/sequest/expt105.mzid");
		// writer.setWriter(new BufferedWriter(new OutputStreamWriter((System.out))));
		writer.start();
		writer.end();
		
		// To validate against schema:
		// xmllint --noout --schema /Users/vagisha/Desktop/mzIdentML/svn/schema/mzIdentML1.1.0.xsd expt105.mzid
	}
}
