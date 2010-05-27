/**
 * GOSlimStatsCalculator.java
 * @author Vagisha Sharma
 * May 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;

/**
 * 
 */
public class GOSlimStatsCalculator {

	private List<Integer> nrseqProteinIds;
	private int goSlimTermId = -1;
	private int goAspect;
	
	private List<GOSlimTerm> termNodes;
	private int numProteinsNotAnnotated = 0;
	
	
	public List<Integer> getNrseqProteinIds() {
		return nrseqProteinIds;
	}
	public void setNrseqProteinIds(List<Integer> nrseqProteinIds) {
		this.nrseqProteinIds = nrseqProteinIds;
	}
	public void setGoSlimTermId(int goSlimTermId) {
		this.goSlimTermId = goSlimTermId;
	}
	public int getGoAspect() {
		return goAspect;
	}
	public void setGoAspect(int goAspect) {
		this.goAspect = goAspect;
	}
	
	public void calculate() throws GOException {
		
		if(nrseqProteinIds == null || nrseqProteinIds.size() == 0) {
			throw new GOException("No nrseq protein IDs found");
		}
		
		if(goSlimTermId == -1)
			throw new GOException("No GO Slim term ID found");
		
		if(goAspect != GOUtils.BIOLOGICAL_PROCESS && 
		   goAspect != GOUtils.MOLECULAR_FUNCTION &&
		   goAspect != GOUtils.CELLULAR_COMPONENT) {
			
			throw new GOException("Invalid GO aspect: "+goAspect+
					". GO aspect should be one of: "+GOUtils.BIOLOGICAL_PROCESS+" or "+
					GOUtils.MOLECULAR_FUNCTION+" or "+
					GOUtils.CELLULAR_COMPONENT);
		}
		
		// Get the GO terms for the given GO Slim
		List<GONode> nodes = null;
		try {
			nodes = GOSlimUtils.getGOSlimTerms(goSlimTermId, goAspect);
		} catch (SQLException e) {
			throw new GOException("Error getting terms for GO Slim Term ID: "+goSlimTermId, e);
		}
		
		Map<Integer, GOSlimTerm> slimTermMap = new HashMap<Integer, GOSlimTerm>();
		for(GONode node: nodes) {
			slimTermMap.put(node.getId(), new GOSlimTerm(node, nrseqProteinIds.size()));
		}
		
		
		for(int nrseqProteinId: nrseqProteinIds) {
			
			List<GONode> annotNodes = null;
			try {
				annotNodes = GOSlimUtils.getAnnotations(nrseqProteinId, goSlimTermId, goAspect);
			} catch (SQLException e) {
				throw new GOException("Error getting terms for GO annotations for protein: "+nrseqProteinId, e);
			}
			
			if(annotNodes.size() == 0)
				this.numProteinsNotAnnotated++;
			
			for(GONode annot: annotNodes) {
				GOSlimTerm slimTerm = slimTermMap.get(annot.getId());
				if(slimTerm != null) {
					slimTerm.addProteinIdForTerm(nrseqProteinId);
				}
				else {
					throw new GOException("Term "+annot.getAccession()+" not a member of GO Slim: "+goSlimTermId);
				}
			}
		}
		
		this.termNodes = new ArrayList<GOSlimTerm>(slimTermMap.values());
	}
	
	public GOSlimAnalysis getAnalysis() {
		GOSlimAnalysis analysis = new GOSlimAnalysis();
		analysis.setNrseqProteinIds(this.nrseqProteinIds);
		analysis.setNumProteinsNotAnnotated(this.numProteinsNotAnnotated);
		Collections.sort(termNodes, new Comparator<GOSlimTerm>() {
			@Override
			public int compare(GOSlimTerm o1, GOSlimTerm o2) {
				return Integer.valueOf(o2.getProteinCountForTerm()).compareTo(o1.getProteinCountForTerm());
			}
		});
		analysis.setTermNodes(this.termNodes);
		if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
			analysis.setGoAspect("Biological Process");
		else if(goAspect == GOUtils.MOLECULAR_FUNCTION)
			analysis.setGoAspect("Molecular Function");
		else if(goAspect == GOUtils.CELLULAR_COMPONENT)
			analysis.setGoAspect("Cellular Component");
		return analysis;
	}
	
	
	public List<GOSlimTerm> getGOSlimTerms() {
		return termNodes;
	}
	
	public int getNumProteinsNotAnnotated() {
		return this.numProteinsNotAnnotated;
	}
}
