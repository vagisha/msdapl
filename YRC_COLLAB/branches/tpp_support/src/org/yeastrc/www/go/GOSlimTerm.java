/**
 * GoSlimGOTerm.java
 * @author Vagisha Sharma
 * May 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.www.util.RoundingUtils;

/**
 * 
 */
public class GOSlimTerm {

	private final GONode goNode;
	private int totalProteins;  // all proteins being considered
	private Set<Integer> proteinIdsForTerm; // proteins annotated with this term
	private Set<Integer> proteinIdsForExactTerm; // proteins annotated with this exact term
	
	public GOSlimTerm(GONode goNode, int totalProteins) {
        this.goNode = goNode;
        this.totalProteins = totalProteins;
        proteinIdsForTerm = new HashSet<Integer>();
        proteinIdsForExactTerm = new HashSet<Integer>();
    }
	
	public void addProteinIdForTerm(int nrseqProteinId) {
		proteinIdsForTerm.add(nrseqProteinId);
	}
	
	public void addProteinIdForExactTerm(int nrseqProteinId) {
		proteinIdsForExactTerm.add(nrseqProteinId);
	}
	
	public int getProteinCountForTerm() {
		return proteinIdsForTerm.size();
	}
	
	public int getProteinCountForExactTerm() {
		return proteinIdsForExactTerm.size();
	}
	
	public double getProteinCountForTermPerc() {
		return (RoundingUtils.getInstance().roundOne(((double)getProteinCountForTerm()*100.0) / (double)totalProteins));
	}
	
	public GONode getGoNode() {
        return goNode;
    }
	
	public String getName() {
		return goNode.getName();
	}
	public String getShortName() {
		return goNode.getNameMax40();
	}
	
	public String getAccession() {
		return goNode.getAccession();
	}
	
	public String getTreeLabel() {
		return this.getName() + "\n[" + this.getAccession() + "]"
		+"\n#Annot. "+this.getProteinCountForTerm()
		+"\n#Exact "+this.getProteinCountForExactTerm();
	}
}
