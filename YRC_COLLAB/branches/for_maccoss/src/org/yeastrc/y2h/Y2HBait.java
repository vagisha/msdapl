/*
 * Y2HBait.java
 * Created on Apr 1, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.y2h;

import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.bio.protein.*;
import java.util.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Apr 1, 2005
 */

public class Y2HBait {

	/**
	 * @return Returns the fullLength.
	 */
	public boolean isFullLength() {
		return fullLength;
	}
	/**
	 * @param fullLength The fullLength to set.
	 */
	public void setFullLength(boolean fullLength) {
		this.fullLength = fullLength;
	}
	/**
	 * @return Returns the mutations.
	 */
	public Set getMutations() {
		return mutations;
	}

	/**
	 * Add the supplied Y2HBaitMutation to this Y2HBait
	 * @param mut
	 */
	public void addMutation(Y2HBaitMutation mut) {
		if (mut == null) return;
		
		if (this.mutations == null)
			this.mutations = new HashSet();
		
		this.mutations.add(mut);
	}
	
	/**
	 * Add the supplied mutation to this bait.
	 * @param position The position mutated
	 * @param orig original amino acid
	 * @param mut mutant amino acid
	 */
	public void addMutation(int position, String orig, String mut) {
		Y2HBaitMutation mutation = new Y2HBaitMutation();
		mutation.setPosition(position);
		mutation.setOrigAminoAcid(orig);
		mutation.setNewAminoAcid(mut);
		
		this.addMutation(mutation);
	}
	
	/**
	 * Remove the supplied Y2HBaitMutation from this Y2HBait
	 * @param mut
	 * @return
	 */
	public boolean removeMutation(Y2HBaitMutation mut) {
		if (mut == null || this.mutations == null) return false;
		
		return this.mutations.remove(mut);
	}
	
	/**
	 * Clear all Y2HBaitMutations from this Y2HBait
	 * @return
	 */
	public void clearMutations() {
		this.mutations = null;
	}
	
	/**
	 * @return Returns the peptide.
	 */
	public Peptide getPeptide() {
		return peptide;
	}
	/**
	 * @param peptide The peptide to set.
	 */
	public void setPeptide(Peptide peptide) {
		this.peptide = peptide;
	}
	/**
	 * @return Returns the protein.
	 */
	public NRProtein getProtein() {
		return protein;
	}
	/**
	 * @param protein The protein to set.
	 */
	public void setProtein(NRProtein protein) {
		this.protein = protein;
	}
	/**
	 * @return Returns the startResidue.
	 */
	public int getStartResidue() {
		return startResidue;
	}
	/**
	 * @param startResidue The startResidue to set.
	 */
	public void setStartResidue(int startResidue) {
		this.startResidue = startResidue;
	}
	/**
	 * @return Returns the stopResidue.
	 */
	public int getEndResidue() {
		return endResidue;
	}
	/**
	 * @param stopResidue The stopResidue to set.
	 */
	public void setEndResidue(int stopResidue) {
		this.endResidue = stopResidue;
	}
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	private NRProtein protein;
	private boolean fullLength;
	private int startResidue;
	private int endResidue;
	private Peptide peptide;
	private Set mutations;
	private int id;
}
