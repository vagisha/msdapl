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

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.TaxonomySearcher;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrProtein;
import org.yeastrc.www.go.GOSlimAnalysis.ProteinSpecies;

/**
 * 
 */
public class GOSlimStatsCalculator {

	private List<Integer> nrseqProteinIds;
	private int goSlimTermId = -1;
	private int goAspect;
	
	private List<GOSlimTerm> termNodes;
	private int numProteinsNotAnnotated = 0;
	
	private static final Logger log = Logger.getLogger(GOSlimStatsCalculator.class.getName());
	
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
		analysis.setTotalProteinCount(nrseqProteinIds.size());
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
		
		try {
			List<GONode> slimNodes = GOSlimUtils.getGOSlims();
			for(GONode node: slimNodes) {
				if(node.getId() == this.goSlimTermId)
					analysis.setGoSlimName(node.getName());
			}
		} catch (SQLException e) {
			analysis.setGoSlimName("ERROR getting GOSlim name");
		}
		
		// species information
		Map<Integer, ProteinSpecies> map = new HashMap<Integer, ProteinSpecies>();
		for(int nrseqId: nrseqProteinIds) {
			NrProtein prot = NrSeqLookupUtil.getNrProtein(nrseqId);
			ProteinSpecies ps = map.get(prot.getSpeciesId());
			if(ps == null) {
				ps = new ProteinSpecies();
				ps.setSpeciesId(prot.getSpeciesId());
				try {
					ps.setSpeciesName(TaxonomySearcher.getInstance().getName(prot.getSpeciesId()));
				} catch (SQLException e) {
					log.error("Error getting species name for: "+prot.getSpeciesId());
					ps.setSpeciesName("UNKNOWN");
				}
				map.put(prot.getSpeciesId(), ps);
			}
			ps.incrCount();
		}
		analysis.setProteinSpecies(new ArrayList<ProteinSpecies>(map.values()));
		
		return analysis;
	}
	
	
	public List<GOSlimTerm> getGOSlimTerms() {
		return termNodes;
	}
	
	public int getNumProteinsNotAnnotated() {
		return this.numProteinsNotAnnotated;
	}
}
