/**
 * 
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * GOSlimAnalysis.java
 * @author Vagisha Sharma
 * May 26, 2010
 * 
 */
public class GOSlimAnalysis {

	private String goAspect;
	private List<Integer> nrseqProteinIds;
	private List<GOSlimTerm> termNodes;
	private int numProteinsNotAnnotated = 0;
	
	public List<Integer> getNrseqProteinIds() {
		return nrseqProteinIds;
	}
	public int getTotalProteinCount() {
		return nrseqProteinIds.size();
	}
	public void setNrseqProteinIds(List<Integer> nrseqProteinIds) {
		this.nrseqProteinIds = nrseqProteinIds;
	}
	public List<GOSlimTerm> getTermNodes() {
		return termNodes;
	}
	public void setTermNodes(List<GOSlimTerm> termNodes) {
		this.termNodes = termNodes;
	}
	public int getNumProteinsNotAnnotated() {
		return numProteinsNotAnnotated;
	}
	public void setNumProteinsNotAnnotated(int numProteinsNotAnnotated) {
		this.numProteinsNotAnnotated = numProteinsNotAnnotated;
	}
	public String getGoAspect() {
		return goAspect;
	}
	public void setGoAspect(String goAspect) {
		this.goAspect = goAspect;
	}
}
