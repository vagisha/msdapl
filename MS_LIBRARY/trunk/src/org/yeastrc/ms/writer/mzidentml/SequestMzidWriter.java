/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputSpectraType;
import org.yeastrc.ms.writer.mzidentml.jaxb.InputsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.MzIdentMLType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchDatabaseRefType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationType;

/**
 * SequestMzidWriter.java
 * @author Vagisha Sharma
 * Aug 15, 2011
 * 
 */
public abstract class SequestMzidWriter {

	private static final String SPEC_ID_LIST1_ID = "SpecIdList1";

	private static final String SPEC_IDENT1_ID = "SpecIdent1";

	private static final String ENCODING = "UTF-8";
	
	// controlled vocabularies
	private CvType psiCv;
	private CvType unimodCv;
	private CvType unitOntologyCv;
	
	private String outputFilePath = null;
	
	private BufferedWriter writer = null;
	private Marshaller marshaller = null;
	
	// unimod repository
	private UnimodRepository unimodRepository;
	
	private void setUnimodRepository(UnimodRepository repository) throws MzIdentMlWriterException {
		
		if(repository == null) {
			throw new MzIdentMlWriterException("Unimod repository cannot be null");
		}
		this.unimodRepository = repository;
	}
	
	UnimodRepository getUnimodRepository() {
		return this.unimodRepository;
	}
	
	private void initializeUnimodRepository() throws MzIdentMlWriterException {
		
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
	
	public void setWriter(BufferedWriter writer) throws MzIdentMlWriterException {
		
		if(writer == null) {
			throw new MzIdentMlWriterException("Writer was null");
		}
		else
			this.writer = writer;
	}
	
	private void initialize() throws MzIdentMlWriterException {
		
		// initialize the unimod repository for modification lookup
		if(this.unimodRepository == null) {
			initializeUnimodRepository();
		}
		
		initializeFieldsBeforeWrite();
		
	}

	public void start() throws MzIdentMlWriterException {
		
		initialize();
		
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
	
	private AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware(this.getSequestVersion());
		swList.add(sequestSw);
		
		return listType;
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

	private void writeAnalysisCollection() throws JAXBException {
		
		AnalysisCollectionType acType = getAnalysisCollection();
		marshaller.marshal(acType, writer);
	}
	
	private AnalysisCollectionType getAnalysisCollection() {
		
		AnalysisCollectionType acType = new AnalysisCollectionType();
		
		SpectrumIdentificationType specId = new SpectrumIdentificationType();
		specId.setId(SPEC_IDENT1_ID);
		acType.getSpectrumIdentification().add(specId);
		
		specId.setSpectrumIdentificationListRef(SPEC_ID_LIST1_ID);
		
		specId.setSpectrumIdentificationProtocolRef(AnalysisProtocolCollectionMaker.SEQUEST_PROTOCOL_ID);
		
		List<InputSpectraType> inputSpectra = getInputSpectraList();
		specId.getInputSpectra().addAll(inputSpectra);
		
		SearchDatabaseRefType searchDbRef = new SearchDatabaseRefType();
		searchDbRef.setSearchDatabaseRef(this.getFastaFileName());
		specId.getSearchDatabaseRef().add(searchDbRef);
		
		return acType;
	}

	private void writeAnalysisProtocolCollection() throws MzIdentMlWriterException, JAXBException {
		
		AnalysisProtocolCollectionType collection = getAnalysisProtocolCollection();
		
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
		
		InputsType inputs = getInputs();
		
		try {
			marshaller.marshal(inputs, writer);
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling InputsType element", e);
		}
	}
	
	private void writeAnalysisData() throws MzIdentMlWriterException, IOException {
		
		AnalysisDataWriter adataWriter = new AnalysisDataWriter();
		adataWriter.setMarshaller(marshaller);
		adataWriter.setWriter(writer);
		
		adataWriter.start();
		adataWriter.startSpectrumIdentificationList(SPEC_ID_LIST1_ID);
		
		writeSearchResults();
		
		adataWriter.endSpectrumIdentificationList();
		adataWriter.end();
		
	}

	void writeSearchResult(SpectrumIdentificationResultType result)
			throws MzIdentMlWriterException {
		
		if(result.getSpectrumIdentificationItem().size() > 0) {
			try {
				marshaller.marshal(result, writer);
				writer.newLine();
			}
			catch (JAXBException e) {
		    	throw new MzIdentMlWriterException("Error marshalling SpectrumIdentificationResultType", e);
			}
			catch(IOException e) {
				throw new MzIdentMlWriterException("Error writing to file", e);
			}
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

	// --------------------------------------------------------------------------------------------
	// Abstract methods
	abstract void initializeFieldsBeforeWrite() throws MzIdentMlWriterException;
	
	abstract String getSequestVersion();
	
	abstract String getFastaFilePath();
	
	abstract String getFastaFileName();
	
	abstract void writeDbSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException;
	
	abstract void writePeptideSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException;
	
	abstract void writePeptideEvidences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException;
	
	abstract List<InputSpectraType> getInputSpectraList();
	
	abstract AnalysisProtocolCollectionType getAnalysisProtocolCollection() throws MzIdentMlWriterException, JAXBException;
	
	abstract InputsType getInputs() throws MzIdentMlWriterException;
	
	abstract void writeSearchResults() throws MzIdentMlWriterException;
	
	// --------------------------------------------------------------------------------------------
}
