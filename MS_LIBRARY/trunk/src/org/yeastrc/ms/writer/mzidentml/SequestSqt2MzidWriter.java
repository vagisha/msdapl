/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;
import org.yeastrc.ms.writer.mzidentml.jaxb.DBSequenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.MzIdentMLType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideEvidenceType;
import org.yeastrc.ms.writer.mzidentml.jaxb.PeptideType;

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
	
	private String sqtFilePath = null;
	
	// modifications
	private List<MsResidueModificationIn> dynamicResidueMods;
	private List<MsResidueModificationIn> staticResidueMods;
	
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
	
	public void setSqtFilePath(String sqtFilePath) throws MzIdentMlWriterException {
		
		if(!(new File(sqtFilePath).exists())) {
			throw new MzIdentMlWriterException("SQT file does not exist: "+sqtFilePath);
		}
		this.sqtFilePath = sqtFilePath;
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
	        
	        writeSequenceCollection();
	        writer.newLine();
	        
	        
		} catch (JAXBException e) {
			throw new MzIdentMlWriterException("Error marshalling data", e);
		} 
		catch (IOException e) {
			throw new MzIdentMlWriterException("Error writing data", e);
		}
	}
	
	protected void writeSequenceCollection() throws IOException, MzIdentMlWriterException {
		
		SequenceCollectionWriter seqCollWriter = new SequenceCollectionWriter();
		seqCollWriter.setWriter(writer);
		seqCollWriter.setMarshaller(marshaller);
		
        seqCollWriter.startCollection();
        writer.newLine();
        
        readSqtModifications();
        
        writeDbSequences(seqCollWriter);
        
        writePeptideSequences(seqCollWriter);
        
        writePeptideEvidences(seqCollWriter);
        
        seqCollWriter.endCollection();
        writer.newLine();
	}
	
	private void readSqtModifications() throws MzIdentMlWriterException {
		
		SequestSQTFileReader sqtReader = new SequestSQTFileReader();
		
		try {
        	
        	sqtReader.open(this.sqtFilePath);
        	SQTHeader sqtHeaders = sqtReader.getSearchHeader();
        	
        	// read modifications from the header
        	readModificationsFromSqtHeader(sqtHeaders);
		}
		
		catch(DataProviderException e) {
        	throw new MzIdentMlWriterException("Error getting data from sqt file", e);
        } 
        finally {
        	sqtReader.close(); // close the file handle
        }
        	
	}
	
	private void writeDbSequences(SequenceCollectionWriter seqCollWriter) throws IOException, MzIdentMlWriterException {
		
		Set<String> proteinAccessions = new HashSet<String>();
		
		// start reading the sqt file
        SequestSQTFileReader sqtReader = new SequestSQTFileReader();
        
        try {
        	
        	sqtReader.open(this.sqtFilePath);
        	SQTHeader sqtHeaders = sqtReader.getSearchHeader();
        	
        	String databaseName = null; // name of the fasta file used for search
        	
        	for(SQTHeaderItem header: sqtHeaders.getHeaders()) {
        		
        		if(header.getName().equalsIgnoreCase("Database")) {
        			databaseName = header.getValue();
        			
        			if(!(StringUtils.isBlank(databaseName))) {
        				
        				databaseName = new File(databaseName).getName();
        			}
        		}
        	}
        	
        	if(StringUtils.isBlank(databaseName)) {
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
        					seqMaker.setSearchDatabase(databaseName);
        					
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
	
	private void readModificationsFromSqtHeader(SQTHeader sqtHeaders) throws MzIdentMlWriterException {
		
		this.dynamicResidueMods = new ArrayList<MsResidueModificationIn>();
		this.staticResidueMods = new ArrayList<MsResidueModificationIn>();
		
		for(SQTHeaderItem headerItem: sqtHeaders.getHeaders()) {
			
			if(headerItem.getName().equals("StaticMod")) {
				
				readStaticMod(headerItem);
				
			}
			
			else if(headerItem.getName().equals("DiffMod")) {
				
				readDynamicMod(headerItem);
				
			}
		}
	}

	private void readDynamicMod(SQTHeaderItem headerItem) throws MzIdentMlWriterException {
		
		// Diff mods string from the SQT file header should look like this
		// STY*=+80.000
		// Multiple dynamic modifications should be present on separate DiffMod lines in a SQT file
		
		String modString = headerItem.getValue();
		
			
		String[] tokens = modString.split("=");

		if (tokens.length < 2)
			throw new MzIdentMlWriterException("Invalid dynamic modification string in sqt file: "+modString);
		if (tokens.length > 2)
			throw new MzIdentMlWriterException("Invalid dynamic modification string in sqt file (appears to have > 1 dynamic modification): "+modString);

		String modChars = tokens[0].trim();
		// get the modification symbol (this character should follow the modification residue characters)
		// example S* -- S is the modified residue; * is the modification symbol
		if (modChars.length() < 2)
			throw new MzIdentMlWriterException("No modification symbol found: "+modString);

		// remove the modification symbol and convert modification chars to upper case 
		modChars = modChars.substring(0, modChars.length()-1).toUpperCase();
		if (modChars.length() < 1)
			throw new MzIdentMlWriterException("No residues found for dynamic modification: "+modString);


		String modMass = tokens[1].trim();
		BigDecimal modMassBd;


		try { modMassBd = new BigDecimal(modMass); }
		catch(NumberFormatException e) {
			throw new MzIdentMlWriterException("Error parsing dynamic modification mass in sqt file: "+modMass);
		}

		// this modification may be for multiple residues; 
		// add one for each residue character
		for (int i = 0; i < modChars.length(); i++) {

			ResidueModification mod = new ResidueModification();
			mod.setModificationMass(modMassBd);
			mod.setModifiedResidue(modChars.charAt(i));

			dynamicResidueMods.add(mod);
		}
		
	}

	private void readStaticMod(SQTHeaderItem headerItem) throws MzIdentMlWriterException {
		
		
		// Example: C=160.139
		
		String modString = headerItem.getValue();
		
		String[] tokens = modString.split("=");
		
		if (tokens.length < 2)
		    throw new MzIdentMlWriterException("Invalid static modification string in sqt file: "+modString);
		if (tokens.length > 2)
		    throw new MzIdentMlWriterException("Invalid static modification string in sqt file (appears to have > 1 static modification): "+modString);


		// convert modification chars to upper case 
		String modChars = tokens[0].trim().toUpperCase();
		String modMass = tokens[1].trim();
		BigDecimal modMassBd;
		
		try {
		    modMassBd = new BigDecimal(modMass);
		}
		catch(NumberFormatException e) {
		    throw new MzIdentMlWriterException("Error parsing static modification mass in sqt file: "+modMass);
		}
		
		// this modification may be for multiple residues; 
		// add one for each residue character
		for (int i = 0; i < modChars.length(); i++) {
			
			ResidueModification mod = new ResidueModification();
			mod.setModificationMass(modMassBd);
			mod.setModifiedResidue(modChars.charAt(i));
			
			staticResidueMods.add(mod);
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
	
	private AnalysisSoftwareListType getAnalysisSoftware() {
		
		AnalysisSoftwareListType listType = new AnalysisSoftwareListType();
		
		List<AnalysisSoftwareType> swList = listType.getAnalysisSoftware();
		
		AnalysisSoftwareType sequestSw = new AnalysisSoftwareMaker().makeSequestAnalysisSoftware("TODO");
		swList.add(sequestSw);
		
		return listType;
	}
	
	public static void main(String[] args) throws MzIdentMlWriterException {
		
		SequestSqt2MzidWriter writer = new SequestSqt2MzidWriter();
		
		String sqtFile = "/Users/vagisha/WORK/MSDaPl_data/chemostat_addLoci/sequest/09Sep10-chemostat-PP-02.sqt";
		
		writer.setSqtFilePath(sqtFile);
		
		writer.setWriter(new BufferedWriter(new OutputStreamWriter((System.out))));
		writer.start();
		writer.end();
	}
}
