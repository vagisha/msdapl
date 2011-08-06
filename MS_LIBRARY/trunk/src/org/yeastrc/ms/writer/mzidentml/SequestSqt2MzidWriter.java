/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.FileFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.MzIdentMLType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseRefType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectraDataType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIDFormatType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationType;
import org.yeastrc.ms.writer.mzidentml.jaxb.UserParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType.SourceFile;

/**
 * SequestSqt2MzidWriter.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class SequestSqt2MzidWriter {

	private static final String ENCODING = "UTF-8";
	
	// controlled vocabularies
	private CvType psiCv;
	private CvType unimodCv;
	private CvType unitOntologyCv;
	
	private String outputFilePath = null;
	
	private BufferedWriter writer = null;
	private Marshaller marshaller = null;
	
	private SequestParamsParser seqParamsparser;
	private String sequestParamsDir = null;
	
	private String sqtFilePath = null;
	private String filename = null;
	
	// modifications
	private List<MsResidueModificationIn> dynamicResidueMods;
	private List<MsResidueModificationIn> staticResidueMods;
	
	// fasta database
	private String fastaFilePath;
	private String fastaFileName;
	
	// unimod repository
	private final UnimodRepository unimodRepository;

	
	
	public SequestSqt2MzidWriter() throws MzIdentMlWriterException {
		
		unimodRepository = new UnimodRepository();
		try {
			unimodRepository.initialize();
		} catch (UnimodRepositoryException e) {
			throw new MzIdentMlWriterException("There was an error initializing the Unimod repository", e);
		}
	}
	
	public void setOutputFilePath(String outputFilePath) throws MzIdentMlWriterException {
		
		this.outputFilePath = outputFilePath;
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFilePath));
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error opening output file: "+outputFilePath, e);
		}
	}
	
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

	public void setWriter(BufferedWriter writer) throws MzIdentMlWriterException {
		
		if(writer == null) {
			throw new MzIdentMlWriterException("Writer was null");
		}
		else
			this.writer = writer;
	}

	public void start() throws MzIdentMlWriterException {
		
		try {
			
			writer.write("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
			writer.newLine();
			writer.write("<MzIdentML ");
			writer.newLine();
			writer.write("\txmlns=\"http://psidev.info/psi/pi/mzIdentML/1.1\"");
			writer.newLine();
			writer.write("\txmlns:psi-pi=\"http://psidev.info/psi/pi/mzIdentML/1.1\"");
			writer.newLine();
			writer.write("\txmlns:pf=\"http://psidev.info/fuge-light/1.1\"");
			writer.newLine();
			writer.write("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			writer.write("xsi:schemaLocation=\"http://psidev.info/psi/pi/mzIdentML/1.1 ../../schema/mzIdentML1.1.0.xsd\" ");
			writer.write("id=\""+this.outputFilePath+"\" ");
			writer.write("version=\"1.1.0\">");
			
			writer.newLine();
			
			
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing to file: "+outputFilePath, e);
		}
		
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(MzIdentMLType.class);
			
			marshaller = jc.createMarshaller();
	        marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        marshaller.setProperty( Marshaller.JAXB_ENCODING, ENCODING);
	        
	        CVListType cvl = getCVlist();
	        marshaller.marshal(cvl, writer);
	        writer.newLine();
	        
	        // read the sequest params file
	        readSequestParams();
	        
	        AnalysisSoftwareListType swlist = getAnalysisSoftware();
	        marshaller.marshal(swlist, writer);
	        writer.newLine();
	        
	        writeSequenceCollection();
	        writer.newLine();
	        
	        writeAnalysisCollection();
	        writer.newLine();
	       
	        writeAnalysisProtocolCollection();
	        writer.newLine();
	        
	        writeDataCollection();
	        writer.newLine();
	        
	        
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling data", e);
		} 
		catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing data", e);
		}
	}
	
	private void readSequestParams() {
		
		seqParamsparser = new SequestParamsParser();
		
		try {
			
			seqParamsparser.parseParams(null, this.sequestParamsDir);
			this.dynamicResidueMods = seqParamsparser.getDynamicResidueMods();
			this.staticResidueMods = seqParamsparser.getStaticResidueMods();
			this.fastaFilePath = seqParamsparser.getSearchDatabase().getServerPath();
			this.fastaFileName = new File(this.fastaFilePath).getName();
			
		} catch (DataProviderException e) {
			e.printStackTrace();
		}
		
	}

	private void writeAnalysisProtocolCollection() throws IOException, MzIdentMlWriterException, JAXBException {
		
		AnalysisProtocolCollectionMaker collMaker = new AnalysisProtocolCollectionMaker(this.unimodRepository);
		
		AnalysisProtocolCollectionType collection = collMaker.getSequestAnalysisProtocol(this.seqParamsparser);
		
		marshaller.marshal(collection, writer);
	}
	
	private void writeDataCollection() throws IOException, MzIdentMlWriterException {
		
		writer.write("<DataCollection>");
		writer.newLine();
		
		writeInputs();
		writer.newLine();
		
		writeAnalysisData();
		writer.newLine();
		
		writer.write("</DataCollection>");
		writer.newLine();
	}
	
	private void writeInputs() throws IOException, MzIdentMlWriterException {
		
		InputsType inputs = new InputsType();
		
		// Source file (the sqt file)
		SourceFile srcFile = new SourceFile();
		inputs.getSourceFile().add(srcFile);
		srcFile.setId(this.filename);
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
		spectraData.setId(this.filename);
		
		SpectrumIDFormatType specIdFmt = new SpectrumIDFormatType();
		specIdFmt.setCvParam(CvParamMaker.getInstance().make("MS:1000776", "scan number only nativeID format", CvConstants.PSI_CV));
		spectraData.setSpectrumIDFormat(specIdFmt);
		
		String cms2file = this.sqtFilePath.replace(".sqt", ".cms2");
		spectraData.setLocation(cms2file);
		fileFormat = new FileFormatType();
		fileFormat.setCvParam(CvParamMaker.getInstance().make("MS:1001466", "MS2 file", CvConstants.PSI_CV));
		spectraData.setFileFormat(fileFormat);
		
		
		try {
			marshaller.marshal(inputs, writer);
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling InputsType element", e);
		}
	}
	

	private void writeAnalysisData() throws IOException, MzIdentMlWriterException {
		
		AnalysisDataWriter adataWriter = new AnalysisDataWriter();
		adataWriter.setMarshaller(marshaller);
		adataWriter.setWriter(writer);
		
		adataWriter.start();
		adataWriter.startSpectrumIdentificationList(this.filename);
		
		writeSearchResults();
		
		adataWriter.endSpectrumIdentificationList();
		adataWriter.end();
		
	}

	private void writeSearchResults() throws MzIdentMlWriterException, IOException {
		
		
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
        		specResultMaker.initSpectrumResult(filename, scanResult.getScanNumber());
        		
        		for(SequestSearchResultIn psm: psmList) {
        			
        			specResultMaker.addSequestResult(psm);
        		}
        		
        		SpectrumIdentificationResultType result = specResultMaker.getSpectrumResult();
        		
        		if(result.getSpectrumIdentificationItem().size() > 0) {
        			marshaller.marshal(result, writer);
        			writer.newLine();
        		}
        	}
        }
        catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } catch (JAXBException e) {
        	throw new MzIdentMlWriterException("Error marshalling SpectrumIdentificationResultType", e);
		}catch (ModifiedSequenceBuilderException e) {
        	throw new MzIdentMlWriterException("Error building modified peptide sequence for psm", e);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
		
	}

	protected void writeSequenceCollection() throws IOException, MzIdentMlWriterException {
		
		SequenceCollectionWriter seqCollWriter = new SequenceCollectionWriter();
		seqCollWriter.setWriter(writer);
		seqCollWriter.setMarshaller(marshaller);
		
        seqCollWriter.startCollection();
        writer.newLine();
        
        writeDbSequences(seqCollWriter);
        
        writePeptideSequences(seqCollWriter);
        
        writePeptideEvidences(seqCollWriter);
        
        seqCollWriter.endCollection();
	}
	
	private void writeDbSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> proteinAccessions = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	SQTHeader sqtHeaders = sqtReader.getSearchHeader();
        	
        	for(SQTHeaderItem header: sqtHeaders.getHeaders()) {
        		
        		if(header.getName().equalsIgnoreCase("Database")) {
        			String databaseName = header.getValue();
        			
        			if(!(StringUtils.isBlank(databaseName))) {
        				
        				this.fastaFilePath = databaseName;
        				
        				this.fastaFileName = new File(databaseName).getName();
        			}
        		}
        	}
        	
        	if(StringUtils.isBlank(this.fastaFileName)) {
        		throw new MzIdentMlWriterException("Could not find database name in the sqt file");
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
	

	private void writePeptideSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
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
        			
        			PeptideMaker peptideMaker = new PeptideMaker(this.unimodRepository);
        			
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
	
	private void writePeptideEvidences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
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
	
	public void end() throws MzIdentMlWriterException {
		
		try {
			writer.write("</MzIdentML>");
			writer.newLine();
		} catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing to file", e);
		}
		finally {
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
	}

	private CVListType getCVlist(){

        CVListType cvListType = new CVListType();
        List<CvType> cvList = cvListType.getCv();
        
        psiCv = CvConstants.PSI_CV;
        cvList.add(psiCv);
        
        unitOntologyCv = CvConstants.UNIT_ONTOLOGY_CV;
        cvList.add(unitOntologyCv);
        
        unimodCv = CvConstants.UNIMOD_CV;
        cvList.add(unimodCv);
        
        return cvListType;
        
    }
	
	
	private void writeAnalysisCollection() throws JAXBException {
		
		AnalysisCollectionType acType = getAnalysisCollection();
		marshaller.marshal(acType, writer);
	}
	
	private AnalysisCollectionType getAnalysisCollection() {
		
		AnalysisCollectionType acType = new AnalysisCollectionType();
		
		SpectrumIdentificationType specId = new SpectrumIdentificationType();
		specId.setId("SpecIdent1");
		acType.getSpectrumIdentification().add(specId);
		
		specId.setSpectrumIdentificationListRef(this.filename);
		
		specId.setSpectrumIdentificationProtocolRef(AnalysisProtocolCollectionMaker.SEQUEST_PROTOCOL_ID);
		
		InputSpectraType spectra = new InputSpectraType();
		spectra.setSpectraDataRef(this.filename);
		specId.getInputSpectra().add(spectra);
		
		
		SearchDatabaseRefType searchDbRef = new SearchDatabaseRefType();
		searchDbRef.setSearchDatabaseRef(this.fastaFileName);
		specId.getSearchDatabaseRef().add(searchDbRef);
		
		return acType;
	}

	private AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware("TODO");
		swList.add(sequestSw);
		
		return listType;
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
