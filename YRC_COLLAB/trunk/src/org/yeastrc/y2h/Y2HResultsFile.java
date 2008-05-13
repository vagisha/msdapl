/* Y2HResultsFile.java
 * Created on Apr 22, 2004
 */
package org.yeastrc.y2h;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.data.InvalidFileFormatException;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.nr_seq.NRDatabaseUtils;
import org.yeastrc.nr_seq.NR_NCBIProteinSearcher;
import org.yeastrc.orf.InvalidORFException;
import org.yeastrc.orf.ORFFactory;
import org.yeastrc.sgd.SGDUtils;
import org.yeastrc.nr_seq.*;
/**
 * This class represents a results file, usually uploaded by a yeast-two hybrid
 * experimentalist in Stan Fields' lab..  The text file is a tab delimited file
 * of bait and prey hits, and their frequencies.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 22, 2004
 *
 */
public class Y2HResultsFile {

	public Y2HResultsFile() {
		this.text = null;
		this.results = null;
		this.bait = null;
		this.isParsed = false;
		this.screenID = 0;
		this.baitString = null;
	}

	/**
	 * Parses the text that has been set via the setText() method
	 * @throws InvalidFileFormatException If there is a problem with the file format
	 * @throws InvalidORFException If there is an invalid systematic name for an orf in the file
	 * @throws SQLException If there is a database error
	 */
	public void parseText() throws InvalidFileFormatException, InvalidORFException, SQLException, Exception {
		Map preyMap = new HashMap();
		ORFFactory of = ORFFactory.getInstance();

		this.results = new ArrayList();
		this.isParsed = false;
		this.screenID = 0;
		this.baitString = null;
		
		if (this.text == null) { throw new InvalidFileFormatException("Have null for text."); }
		
		// Loop through the lines in the text file
		String[] lines = this.text.split("\r\n");
		if (lines.length <= 1) { lines = this.text.split("\n"); }
		if (lines.length <= 1) { lines = this.text.split("\r"); }

		for ( int i = 0; i < lines.length; i++ ) {
			String[] fields = lines[i].split("\t");

			String tbaitORF = fields[0].trim();
			if (tbaitORF == null || tbaitORF.equals(""))
				throw new Exception("Got a blank bait in the results file...");
			

			// Set the bait ORF if it isn't set, and make sure there is only one bait ORF in the results files
			if (this.baitString == null) {
				this.baitString = tbaitORF;

				// Get the protein ID FROM the YRC_NRSEQ that corresponds to this bait, and set it in the bait.
				if (tbaitORF.startsWith("gi|")) {

					// Attempt to get protein ID by searching NCBI and mapping to YRC_NR
					this.bait = new Y2HBait();
					
					try {
						this.bait.setProtein(NR_NCBIProteinSearcher.getInstance().getProteinFromNCBI(tbaitORF).getProtein());
					} catch (Exception e) {
						throw new Exception ("Error getting protein from NCBI: " + e.getMessage());
					}					
				} else if (tbaitORF.startsWith("yrc:")) {
					
					// They specified a specific YRC protein number
					this.bait = new Y2HBait();
					
					try {
						int yid = Integer.parseInt( tbaitORF.substring( 4, tbaitORF.length() ) );
						this.bait.setProtein( (NRProtein)(NRProteinFactory.getInstance().getProtein( yid ) ));
					} catch( Exception e) {
						throw new Exception ( "Error locating YRC protein: " + tbaitORF );
					}
					
				} else {

					// Attempt to get protein ID by searching SGD for the gene name.
					this.bait = new Y2HBait();
					
					try {
						Species sp = new Species();
						sp.setId(TaxonomyUtils.SACCHAROMYCES_CEREVISIAE);
						this.bait.setProtein(NRDatabaseUtils.getInstance().findProteinByName(tbaitORF, sp));
					} catch (Exception e) {
						throw new Exception ("Error getting protein from SGD: " + e.getMessage());
					}
				}
				
				if (this.bait.getProtein() == null)
					throw new Exception ("Have null for protein in the bait...");
			
			}
			else {
				if (!this.baitString.equals(tbaitORF)) {
					String tmpORF = this.baitString;

					this.bait = null;
					this.baitString = null;
					this.results = null;
					
					throw new InvalidFileFormatException("The results for this screen can only contain a single bait ORF.  The results file contains at least two (" + 
						tmpORF + " and " + tbaitORF + ")");
				}
			}

				//try { tbaitORF = SGDUtils.getSystematicName(fields[0].trim()); }
				//catch (InvalidORFException e) { tbaitORF = fields[0].trim(); }
			

			// Set the prey ORF
			String tpreyORF = null;
			try { tpreyORF = SGDUtils.getSystematicName(fields[1].trim()); }
			catch (InvalidORFException e) { tpreyORF = fields[1].trim(); }
			
			// Skip this line if the prey ORF is blank.
			if (tpreyORF == null || tpreyORF.equals(""))
				continue;
			
			// The line has an invalid number of fields.
			if (fields.length < 3 || fields.length > 4) {
				this.bait = null;
				this.baitString = null;
				this.results = null;
				
				throw new InvalidFileFormatException("Invalid number of arguments (" + fields.length + ") on the following line: " + lines[i]);
			}
			
			// Create a Y2HScreenResult object, and set all the values for this result row
			Y2HScreenResult ysr;
			ysr = (Y2HScreenResult)(preyMap.get(tpreyORF));
			if (ysr == null) {
				ysr = new Y2HScreenResult();
				ysr.setPreyORF(of.getORF(tpreyORF, TaxonomyUtils.SACCHAROMYCES_CEREVISIAE));
			}
			

			// Add hit counts as appropriate
			if (fields[2].equals("x"))
				ysr.addHit();
			else if (!fields[2].equals(""))
				throw new InvalidFileFormatException("Invalid arguments (" + fields[2] + ") on the following line: " + lines[i]);
			
			if (fields.length > 3 && fields[3].equals("x"))
				ysr.addHit();


			preyMap.put(tpreyORF, ysr);
		}

		this.results = new ArrayList(preyMap.values());
		this.isParsed = true;
	}

	/**
	 * Calling this method will save the results from the parsed text file to the database
	 * setScreenID() and parseText() must be called first, or nothing will be saved
	 * @return The number of results saved.
	 * @throws SQLException if there is a database error
	 */
	public int saveResults() throws SQLException, Exception {
		int numSaved = 0;
		
		if (this.screenID == 0 || !this.isParsed) return 0;
		if (this.results == null || this.results.size() < 1) return 0;
		
		Iterator iter = this.results.iterator();
		while (iter.hasNext()) {
			Y2HScreenResult ysr = (Y2HScreenResult)(iter.next());
			if (ysr.getNumHits() < 1) { continue; }
			
			ysr.setScreenID(this.screenID);
			try { ysr.save(); }
			catch (InvalidIDException e) { continue; }
			
			numSaved++;
		}
		
		return numSaved;
	}

	/**
	 * Get the text that is to be/was parsed
	 * @return The text that is to be/was parsed
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Set the text to be parsed for results
	 * @param arg
	 */
	public void setText(String arg) {
		if (arg.equals(this.text)) { return; }

		this.isParsed = false;
		this.text = arg;
	}
	
	/** Set the screen id for the results contained in this file */
	public void setScreenID(int arg) {
		this.screenID = arg;
	}
	
	/** Get the screen id for the results contained in this file */
	public int getScreenID() { return this.screenID; }

	/**
	 * @return
	 */
	public Y2HBait getBait() {
		return this.bait;
	}

	/**
	 * @param string
	 */
	public void setBait(Y2HBait thebait) {
		this.bait = thebait;
	}

	// Instance vars
	private String text;		// text we're parsing
	private List results;		// list of Y2HScreenResult objects corresponding to the results
	private Y2HBait bait;		// the bait ORF in the results file
	private boolean isParsed;	// whether or not we have already parsed the text we have
	private int screenID;		// the database id of the screen to which these results belong
	private String baitString;	// The string used to describe the bait.

}
