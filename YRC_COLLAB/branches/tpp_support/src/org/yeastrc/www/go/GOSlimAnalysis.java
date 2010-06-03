/**
 * 
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;

/**
 * GOSlimAnalysis.java
 * @author Vagisha Sharma
 * May 26, 2010
 * 
 */
public class GOSlimAnalysis {

	private int goAspect;
	private String goAspectString;
	private String goSlimName = "UNKNOWN";
	private GONode aspectRoot;
	private int totalProteinCount;
	private List<GOSlimTerm> termNodes;
	private int numProteinsNotAnnotated = 0;
	private List<SpeciesProteinCount> proteinSpecies;
	
	private static final Logger log = Logger.getLogger(GOSlimAnalysis.class.getName());
	
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
	public List<GOSlimTerm> getTermNodesMinusRootNodes() {
		List<GOSlimTerm> terms = new ArrayList<GOSlimTerm>(this.termNodes.size());
		for(GOSlimTerm term: termNodes) {
			if(term.getGoNode().isRoot())
				continue;
			if(aspectRoot != null && aspectRoot.getId() == term.getGoNode().getId())
				continue;
			terms.add(term);
		}
		return terms;
	}
	public int getNumProteinsNotAnnotated() {
		return numProteinsNotAnnotated;
	}
	public void setNumProteinsNotAnnotated(int numProteinsNotAnnotated) {
		this.numProteinsNotAnnotated = numProteinsNotAnnotated;
	}
	public String getGoAspectString() {
		return goAspectString;
	}
	private void setGoAspectString(String goAspectString) {
		this.goAspectString = goAspectString;
	}
	public int getGoAspect() {
		return goAspect;
	}
	public void setGoAspect(int goAspect) {
		this.goAspect = goAspect;
		if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
			this.setGoAspectString("Biological Process");
		else if(goAspect == GOUtils.MOLECULAR_FUNCTION)
			this.setGoAspectString("Molecular Function");
		else if(goAspect == GOUtils.CELLULAR_COMPONENT)
			this.setGoAspectString("Cellular Component");
		
		try {
			aspectRoot = GOUtils.getAspectRootNode(this.goAspect);
		} catch (Exception e) {
			log.error(e);
		}
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
	
	public boolean isTermsIncludeAspectRoot() {
		for(GOSlimTerm term: termNodes) {
			if(aspectRoot != null && aspectRoot.getId() == term.getGoNode().getId())
				return true;
		}
		return false;
	}
	
	public String getAspectRootName() {
		if(aspectRoot != null)
			return aspectRoot.getName();
		else
			return "NONE";
	}
	
	public List<SpeciesProteinCount> getSpeciesProteinCount() {
		return this.proteinSpecies;
	}
	
	public void setSpeciesProteinCount(List<SpeciesProteinCount> psList) {
		this.proteinSpecies = psList;
	}
	
	
	public static final class SpeciesProteinCount {
		private int speciesId;
		private String speciesName;
		private int count;
		private int annotated; // # with GO annotations
		
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
		public void incrTotal() {
			this.count++;
		}
		public int getAnnotated() {
			return annotated;
		}
		public void incrAnnotated() {
			this.annotated++;
		}
		public int getNotAnnotated() {
			return count - annotated;
		}
	}
}
