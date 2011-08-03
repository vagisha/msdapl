/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;
import org.yeastrc.ms.writer.mzidentml.jaxb.MzIdentMLType;

/**
 * Sequest2MzidWriter.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class Sequest2MzidWriter {

	private static final String ENCODING = "UTF-8";
	
	// controlled vocabularies
	private CvType psiCv;
	private CvType unimodCv;
	private CvType unitOntologyCv;
	
	private String outputFilePath = null;
	
	private BufferedWriter writer = null;
	private Marshaller marshaller = null;
	
	
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
	        
	        
	        AnalysisSoftwareListType swlist = getAnalysisSoftware();
	        marshaller.marshal(swlist, writer);
	        writer.newLine();
	        
	        
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling data", e);
		} 
		catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing data", e);
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
	
	private AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware("TODO");
		swList.add(sequestSw);
		
		return listType;
	}
	
	public static void main(String[] args) throws MzIdentMlWriterException {
		
		Sequest2MzidWriter writer = new Sequest2MzidWriter();
		
		writer.setWriter(new BufferedWriter(new OutputStreamWriter((System.out))));
		writer.start();
		writer.end();
	}
}
