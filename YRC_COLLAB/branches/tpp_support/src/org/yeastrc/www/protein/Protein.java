/**
 * Protein.java
 * @author Vagisha Sharma
 * May 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.protein;

import java.util.HashSet;

import org.yeastrc.ms.domain.nrseq.NrProtein;
import org.yeastrc.ms.util.ProteinUtils;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.www.proteinfer.ProteinSequenceHtmlBuilder;

/**
 * 
 */
public class Protein {

	private String sequence;
	private NrProtein protein;
	private ProteinListing listing;
	
	public int getId() {
		return protein.getId();
	}
	public String getSequence() {
		return sequence;
	}
	public String getHtmlSequence() {
		return ProteinSequenceHtmlBuilder.getInstance().build(sequence, new HashSet<String>(0));
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public NrProtein getProtein() {
		return protein;
	}
	public void setProtein(NrProtein protein) {
		this.protein = protein;
	}
	public ProteinListing getProteinListing() {
		return listing;
	}
	public void setProteinListing(ProteinListing listing) {
		this.listing = listing;
	}
	public double getMolecularWeight() {
		return ProteinUtils.calculateMolWt(sequence);
	}
	public double getPi() {
		return ProteinUtils.calculatePi(sequence);
	}
}
