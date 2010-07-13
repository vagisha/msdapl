/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.bio.go.EvidenceUtils;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.go.Annotation;
import org.yeastrc.www.go.GoTermSearcher;
import org.yeastrc.www.go.ProteinGOAnnotationChecker;

/**
 * ProteinGoTermsFilter.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class ProteinGoTermsFilter {

    
    private static ProteinGoTermsFilter instance;
    
    private static final Logger log = Logger.getLogger(ProteinGoTermsFilter.class.getName());
    
    private ProteinGoTermsFilter() {}
    
    public static ProteinGoTermsFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinGoTermsFilter();
    	return instance;
    }


    public List<Integer> filterPinferProteinsByGoAccession(List<Integer> allProteinIds, GOProteinFilterCriteria filters) throws Exception {
        
    	if(filters == null)
    		return allProteinIds;
    	
    	List<String> goAccessions = filters.getGoAccessions();
    	if(goAccessions == null || goAccessions.size() == 0)
    		return allProteinIds;
    	
    	// Convert the GO accessions into GONode objects
    	List<GONode> goNodes = new ArrayList<GONode>(goAccessions.size());
    	for(String goAccession: goAccessions) {
    		goAccession = goAccession.trim();
    		if(goAccession == null || goAccession.length() == 0)
    			continue;
    		GONode node = GOCache.getInstance().getGONode(goAccession);
    		if(node != null)
    			goNodes.add(node);
    	}
    	
    	// Get any evidence codes to exclude
    	List<EvidenceCode> evidenceCodes = new ArrayList<EvidenceCode>();
    	for(String codeStr: filters.getExcludeEvidenceCodes()) {
    		int id = EvidenceUtils.getEvidenceCodeId(codeStr);
    		if(id == -1) {
    			log.error("NO EvidenceCode found for :"+codeStr);
    		}
    		else {
    			EvidenceCode code = EvidenceUtils.getEvidenceCodeInstance(id);
    			evidenceCodes.add(code);
    		}
    	}
    	
    	List<Integer> filteredIds = null;
    	if(!filters.isMatchAllGoTerms())
    		filteredIds = getFilteredAnyMatch(allProteinIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	else
    		filteredIds = getFilteredAllMatch(allProteinIds, filters.isExactAnnotation(), goNodes, evidenceCodes);
    	
        return filteredIds;
    }

    // Returns a list of proteinIds that match any one of the given GO terms
	private List<Integer> getFilteredAnyMatch(List<Integer> allProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		
    		for(GONode node: goNodes) {
    			Annotation annot = null;
    			if(evidenceCodes.size() == 0)
    				annot = ProteinGOAnnotationChecker.isProteinAnnotated(protein.getNrseqProteinId(), node.getId());
    			else
    				annot = ProteinGOAnnotationChecker.isProteinAnnotated(protein.getNrseqProteinId(), node.getId(), evidenceCodes);
    			
    			if(annot == Annotation.NONE)
    				continue;
    			if(exact) {
    				if(annot == Annotation.EXACT) {
    					filteredIds.add(proteinId);
    					break;
    				}
    			}
    			else {
    				filteredIds.add(proteinId);
    				break;
    			}
    		}
    	}
		return filteredIds;
	}
	
	// Returns a list of proteinIds that match all of the given GO terms
	private List<Integer> getFilteredAllMatch(List<Integer> allProteinIds,
			boolean exact, List<GONode> goNodes, List<EvidenceCode> evidenceCodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		
    		boolean matchAll = true;
    		
    		for(GONode node: goNodes) {
    			Annotation annot = null;
    			
    			if(evidenceCodes.size() == 0)
    				annot = ProteinGOAnnotationChecker.isProteinAnnotated(protein.getNrseqProteinId(), node.getId());
    			else
    				annot = ProteinGOAnnotationChecker.isProteinAnnotated(protein.getNrseqProteinId(), node.getId(), evidenceCodes);
    			
    			if(annot == Annotation.NONE) {
    				matchAll = false;
    				break;
    			}
    			if(exact) {
    				if(annot != Annotation.EXACT) {
    					matchAll = false;
    					break;
    				}
    					
    			}
    		}
    		if(matchAll)
    			filteredIds.add(proteinId);
    	}
		return filteredIds;
	}
}
