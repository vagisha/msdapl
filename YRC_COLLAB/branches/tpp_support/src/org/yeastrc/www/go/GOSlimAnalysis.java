/**
 * 
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

/**
 * GOSlimAnalysis.java
 * @author Vagisha Sharma
 * May 26, 2010
 * 
 */
public class GOSlimAnalysis {

	private String goAspect;
	private String goSlimName = "UNKNOWN";
	private int totalProteinCount;
	private List<GOSlimTerm> termNodes;
	private int numProteinsNotAnnotated = 0;
	private List<ProteinSpecies> proteinSpecies;
	
	public int getTotalProteinCount() {
		return totalProteinCount;
	}
	public void setTotalProteinCount(int totalProteinCount) {
		this.totalProteinCount = totalProteinCount;
	}
	public int getSlimTermCount() {
		return termNodes.size();
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
	public String getGoSlimName() {
		return goSlimName;
	}
	public void setGoSlimName(String goSlimName) {
		this.goSlimName = goSlimName;
	}
	
	public int getSpeciesCount() {
		if(proteinSpecies != null)
			return proteinSpecies.size();
		else
			return 0;
	}
	
	public List<ProteinSpecies> getProteinSpecies() {
		return this.proteinSpecies;
	}
	
	public void setProteinSpecies(List<ProteinSpecies> psList) {
		this.proteinSpecies = psList;
	}
	
	
	public static final class ProteinSpecies {
		private int speciesId;
		private String speciesName;
		private int count;
		public int getSpeciesId() {
			return speciesId;
		}
		public void setSpeciesId(int speciesId) {
			this.speciesId = speciesId;
		}
		public String getSpeciesName() {
			return speciesName;
		}
		public void setSpeciesName(String speciesName) {
			this.speciesName = speciesName;
		}
		public int getCount() {
			return count;
		}
		public void incrCount() {
			this.count++;
		}
	}
}
