/**
 * PercolatorXmlFileChecker.java
 * @author Vagisha Sharma
 * Aug 25, 2011
 */
package org.yeastrc.ms.parser.percolator;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;

/**
 * Validates the <peptides> section of a Percolator generated xml file.
 * The "peptide_id" attribute of a <peptide> element should be unique.  However, in some 
 * files there can be duplicates.  I have only seen this, so far, for small peptides.
 * Example:  EVVVR
 * Duplicate peptides have to be removed before uploading to MSDaPl.  
 * 
 * Usage: 
 * java -classpath mslib_fat.jar org.yeastrc.ms.parser.percolator.PercolatorXmlFileChecker <input_percolator_xml>
 * 
 */
public class PercolatorXmlFileChecker {

	public static void main(String[] args) throws DataProviderException {
		
		String filepath = args[0];
		
		PercolatorXmlFileReader reader = new PercolatorXmlFileReader();
		reader.setSearchProgram(Program.SEQUEST);
		reader.setReadDecoyResults(true);
		
		reader.open(filepath);
		
		while(reader.hasNextPsm()) {
			PercolatorXmlResult result = (PercolatorXmlResult) reader.getNextPsm();
			if(result.isDecoy()) {
				// do nothing
			}
			else {
				// do nothing
			}
		}
		
		Set<String> peptides = new HashSet<String>();
		
		while(reader.hasNextPeptide()) {
			
			PercolatorXmlPeptideResult result = (PercolatorXmlPeptideResult) reader.getNextPeptide();
			
			String peptideIdString = result.getResultPeptide().getModifiedPeptidePS();
			if(peptides.contains(peptideIdString)) {
				System.out.println("Found duplicate peptide: "+peptideIdString);
			}
			
			else {
				peptides.add(peptideIdString);
			}
		}
		
		reader.close();
	}
}
