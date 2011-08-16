/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
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
public class SequestSqt2MzidWriter extends SequestMzidWriter {
	
	private SequestParamsParser seqParamsparser;
	private String sequestParamsDir = null;
	
	private String sequestVersion;
	
	private String sqtFilePath = null;
	private String filename = null;
	
	// modifications
	private List<MsResidueModificationIn> dynamicResidueMods;
	private List<MsResidueModificationIn> staticResidueMods;
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	
	public void setSequestParamsDir(String sequestParamsDir) throws MzIdentMlWriterException {
		
		this.sequestParamsDir = sequestParamsDir;
	}
	
	public void setSqtFilePath(String sqtFilePath) throws MzIdentMlWriterException {
		
		if(!(new File(sqtFilePath).exists())) {
			throw new MzIdentMlWriterException("SQT file does not exist: "+sqtFilePath);
		}
		this.sqtFilePath = sqtFilePath;
		
		this.filename = new File(sqtFilePath).getName();
		if(filename.toLowerCase().endsWith(".sqt"));
		this.filename = filename.substring(0, filename.length() - 4);
	}

	void initializeFieldsBeforeWrite() throws MzIdentMlWriterException {
		
		// read the sequest params file
        try {
			readSequestParams();
		} catch (DataProviderException e) {
			throw new MzIdentMlWriterException("Error reading sequest.params file", e);
		}
		
		// read the sequest version from the SQT header
		try {
			readSequestVersion();
		} catch (DataProviderException e) {
			throw new MzIdentMlWriterException("Error reading version of sequest from sqt file header", e);
		}
		
	}
	
	private void readSequestVersion() throws DataProviderException {
		
		SequestSQTFileReader reader = null;
		try {
			
			reader = new SequestSQTFileReader();
			reader.open(this.sqtFilePath);
			SQTHeader header = reader.getSearchHeader();
			this.sequestVersion = header.getSearchEngineVersion();
		}
		finally {
			if(reader != null) reader.close();
		}
	}
	
	private void readSequestParams() throws DataProviderException {
		
		seqParamsparser = new SequestParamsParser();
		
		seqParamsparser.parseParams(null, this.sequestParamsDir);
		this.dynamicResidueMods = seqParamsparser.getDynamicResidueMods();
		this.staticResidueMods = seqParamsparser.getStaticResidueMods();
		this.fastaFilePath = seqParamsparser.getSearchDatabase().getServerPath();
		this.fastaFileName = new File(this.fastaFilePath).getName();

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
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader(); // go past the file header
        	
        	if(StringUtils.isBlank(this.fastaFileName)) {
        		throw new MzIdentMlWriterException("Could not find fasta database name");
        	}

        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			List<MsSearchResultProteinIn> proteins = psm.getProteinMatchList();
        			
        			for(MsSearchResultProteinIn protein: proteins) {
        				
        				String accession = protein.getAccession();
        				
        				if(!proteinAccessions.contains(accession)) {
        					
        					DbSequenceMaker seqMaker = new DbSequenceMaker();
        					seqMaker.setAccession(accession);
        					seqMaker.setId(accession);
        					seqMaker.setSearchDatabase(this.fastaFileName);
        					
        					// if we have a description for this protein in the sqt file add it
        					if(!StringUtils.isBlank(protein.getDescription())) {
        						seqMaker.addDescription(protein.getDescription());
        					}
        					
        					DBSequenceType seqType = seqMaker.make();
        					seqCollWriter.addSequence(seqType);
        					
        					// add to our list
        					proteinAccessions.add(accession);
        				}
        			}
        		}
        		
        	}
        }
        catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}

	void writePeptideSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
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
        			for(MsResidueModificationIn smod: staticResidueMods) {
        				
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
        catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzIdentMlWriterException("There was an error building modified sequence for a peptide", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}

	void writePeptideEvidences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> peptideSequences = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			MsSearchResultPeptide resultPeptide = psm.getResultPeptide();
        			
        			String modifiedSeq = resultPeptide.getModifiedPeptide();
        			
        			
        			if(peptideSequences.contains(modifiedSeq))
        				continue;
        			
        			List<MsSearchResultProteinIn> proteins = psm.getProteinMatchList();
        			
        			int evidenceCount = 1;
        			
        			for(MsSearchResultProteinIn protein: proteins) {
        				
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
        catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling DBSequenceType", e);
		} catch (ModifiedSequenceBuilderException e) {
			throw new MzIdentMlWriterException("There was an error building modified sequence for a peptide", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}

	List<InputSpectraType> getInputSpectraList() {
		
		InputSpectraType spectra = new InputSpectraType();
		spectra.setSpectraDataRef(this.filename+".cms2");
		
		List<InputSpectraType> list = new ArrayList<InputSpectraType>();
		list.add(spectra);
		
		return list;
	}
	
	
	AnalysisProtocolCollectionType getAnalysisProtocolCollection() throws MzIdentMlWriterException, JAXBException {
		
		AnalysisProtocolCollectionMaker collMaker = new AnalysisProtocolCollectionMaker_FromSequestParams(this.getUnimodRepository(), this.seqParamsparser);
		
		return collMaker.getSequestAnalysisProtocol();
	}
	
	
	InputsType getInputs() throws MzIdentMlWriterException {
		
		InputsType inputs = new InputsType();
		
		// Source file (the sqt file)
		SourceFile srcFile = new SourceFile();
		inputs.getSourceFile().add(srcFile);
		srcFile.setId(this.filename+".sqt");
		srcFile.setLocation(this.sqtFilePath);
		FileFormatType fileFormat = new FileFormatType();
		fileFormat.setCvParam(CvParamMaker.getInstance().make("MS:1001563", "Sequest SQT", CvConstants.PSI_CV));
		srcFile.setFileFormat(fileFormat);
		
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
		SpectraDataType spectraData = new SpectraDataType();
		inputs.getSpectraData().add(spectraData);
		spectraData.setId(this.filename+".cms2");
		
		SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
		specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
		spectraData.setSpectrumIDFormat(specIdFmt);
		
		String cms2file = this.sqtFilePath.replace(".sqt", ".cms2");
		spectraData.setLocation(cms2file);
		fileFormat = new FileFormatType();
		fileFormat.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
		spectraData.setFileFormat(fileFormat);
		
		return inputs;
	}
	
	void writeSearchResults() throws MzIdentMlWriterException {
		
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	sqtReader.getSearchHeader();
        	
        	// read the PSMs
        	while(sqtReader.hasNextSearchScan()) {
        		
        		SequestSearchScan scanResult = sqtReader.getNextSearchScan();
        		List<SequestSearchResultIn> psmList = scanResult.getScanResults();
        		
        		SpectrumIdentificationResultMaker specResultMaker = new SpectrumIdentificationResultMaker();
        		specResultMaker.initSpectrumResult(filename+".cms2", scanResult.getScanNumber());
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			specResultMaker.addSequestResult(psm);
        		}
        		
        		SpectrumIdentificationResultType result = specResultMaker.getSpectrumResult();
        		
        		super.writeSearchResult(result);
        	}
        }
        catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } catch (ModifiedSequenceBuilderException e) {
        	throw new MzIdentMlWriterException("Error building modified peptide sequence for psm", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
		
	}
	
	
	
	public static void main(String[] args) throws MzIdentMlWriterException {
		
		SequestSqt2MzidWriter writer = new SequestSqt2MzidWriter();
		
		//String sqtFile = "/Users/vagisha/WORK/MSDaPl_data/chemostat_addLoci/sequest/09Sep10-chemostat-PP-02.sqt";
		//String sqparamsDir = "/Users/vagisha/WORK/MSDaPl_data/chemostat_addLoci/sequest";
		
		String sqtFile = "/Users/silmaril/WORK/UW/MSDaPl_data/jeckels_data/ecoli/sequest/wormy4raw-1.sqt";
		String sqparamsDir = "/Users/silmaril/WORK/UW/MSDaPl_data/jeckels_data/ecoli/sequest";
		
		writer.setSqtFilePath(sqtFile);
		writer.setSequestParamsDir(sqparamsDir);
		
		writer.setOutputFilePath("/Users/silmaril/WORK/UW/MSDaPl_data/jeckels_data/ecoli/sequest/wormy4raw-1.mzid");
		// writer.setWriter(new BufferedWriter(new OutputStreamWriter((System.out))));
		writer.start();
		writer.end();
	}
}
