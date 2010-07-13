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
import org.yeastrc.bio.go.EvidenceUtils;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.TaxonomySearcher;
import org.yeastrc.nrseq.NrsProtein;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.go.GOSlimAnalysis.SpeciesProteinCount;

/**
 * 
 */
public class GOSlimStatsCalculator {

	private List<Integer> nrseqProteinIds;
	private int goSlimTermId = -1;
	private int goAspect;
	private List<String> excludeEvidenceCodes;
	
	private Map<Integer, SpeciesProteinCount> speciesCountMap;
	
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
	public void setExcludeEvidenceCodes(List<String> excludeEvidenceCodes) {
		this.excludeEvidenceCodes = excludeEvidenceCodes;
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
		
		speciesCountMap = new HashMap<Integer, SpeciesProteinCount>();
		
		for(int nrseqProteinId: nrseqProteinIds) {
			
			List<GOAnnotation> annotNodes = null;
			try {
				GOSlimFilter filter = new GOSlimFilter();
				filter.setGoAspect(this.goAspect);
				filter.setSlimTermId(this.goSlimTermId);
				if(excludeEvidenceCodes != null) {
					List<Integer> excludeCodes = new ArrayList<Integer>(excludeEvidenceCodes.size());
					for(String code: excludeEvidenceCodes) {
						int id = EvidenceUtils.getEvidenceCodeId(code);
						if(id != -1)
							excludeCodes.add(id);
					}
					filter.setExcludeEvidenceCodes(excludeCodes);
				}
				annotNodes = GOSlimUtils.getAnnotations(nrseqProteinId, filter);
			} catch (SQLException e) {
				throw new GOException("Error getting terms for GO annotations for protein: "+nrseqProteinId, e);
			}
			
			NrsProtein prot = NrSeqLookupUtil.getNrProtein(nrseqProteinId);
			SpeciesProteinCount spCount = speciesCountMap.get(prot.getSpeciesId());
			if(spCount == null) {
				spCount = initProteinSpeciesCount(prot);
				speciesCountMap.put(prot.getSpeciesId(), spCount);
			}
			spCount.incrTotal();
			if(annotNodes.size() > 0)
				spCount.incrAnnotated();
		
			if(annotNodes.size() == 0)
				this.numProteinsNotAnnotated++;
			
			for(GOAnnotation annot: annotNodes) {
				GOSlimTerm slimTerm = slimTermMap.get(annot.getNode().getId());
				if(slimTerm != null) {
					slimTerm.addProteinIdForTerm(nrseqProteinId);
					if(annot.isExact()) {
						slimTerm.addProteinIdForExactTerm(nrseqProteinId);
					}
				}
				else {
					throw new GOException("Term "+annot.getNode().getAccession()+" not a member of GO Slim: "+goSlimTermId);
				}
			}
		}
		
		this.termNodes = new ArrayList<GOSlimTerm>(slimTermMap.values());
	}
	
	private SpeciesProteinCount initProteinSpeciesCount(NrsProtein prot) {
		SpeciesProteinCount spCount;
		spCount = new SpeciesProteinCount();
		spCount.setSpeciesId(prot.getSpeciesId());
		try {
			spCount.setSpeciesName(TaxonomySearcher.getInstance().getName(prot.getSpeciesId()));
		} catch (SQLException e) {
			log.error("Error getting species name for: "+prot.getSpeciesId());
			spCount.setSpeciesName("UNKNOWN");
		}
		return spCount;
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
		analysis.setGoAspect(goAspect);
		
		try {
			List<GONode> slimNodes = GOSlimUtils.getGOSlims();
			for(GONode node: slimNodes) {
				if(node.getId() == this.goSlimTermId)
					analysis.setGoSlimName(node.getName());
			}
		} catch (SQLException e) {
			analysis.setGoSlimName("ERROR getting GOSlim name");
		}
		List<SpeciesProteinCount> speciesCount = new ArrayList<SpeciesProteinCount>(this.speciesCountMap.values());
		Collections.sort(speciesCount, new Comparator<SpeciesProteinCount>() {
			@Override
			public int compare(SpeciesProteinCount o1, SpeciesProteinCount o2) {
				return Integer.valueOf(o2.getCount()).compareTo(o1.getCount());
			}
		});
		analysis.setSpeciesProteinCount(speciesCount);
		
		return analysis;
	}
	
	
	public List<GOSlimTerm> getGOSlimTerms() {
		return termNodes;
	}
	
	public int getNumProteinsNotAnnotated() {
		return this.numProteinsNotAnnotated;
	}
}
