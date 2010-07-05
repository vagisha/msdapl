/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.go.Annotation;
import org.yeastrc.www.go.GoTermSearcher;

/**
 * ProteinGoTermsFilter.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class ProteinGoTermsFilter {

    
    private static ProteinGoTermsFilter instance;
    
    private ProteinGoTermsFilter() {}
    
    public static ProteinGoTermsFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinGoTermsFilter();
    	return instance;
    }

    public List<Integer> filterPinferProteinsByGoAccession(List<Integer> allProteinIds, String goAccessions, 
    		boolean exact, boolean matchAll) throws Exception {
        
    	String[] goAccessionsArr = goAccessions.split(",");
    	return filterPinferProteinsByGoAccession(allProteinIds, goAccessionsArr, exact, matchAll);
    }

    public List<Integer> filterPinferProteinsByGoAccession(List<Integer> allProteinIds, String[] goAccessions, 
    		boolean exact, boolean matchAll) throws Exception {
        
    	// Convert the GO accessions into GONode objects
    	List<GONode> goNodes = new ArrayList<GONode>(goAccessions.length);
    	for(String goAccession: goAccessions) {
    		if(goAccession == null || goAccession.trim().length() == 0)
    			continue;
    		GONode node = GOCache.getInstance().getGONode(goAccession);
    		if(node != null)
    			goNodes.add(node);
    	}
    	
    	List<Integer> filteredIds = null;
    	if(!matchAll)
    		filteredIds = getFilteredAnyMatch(allProteinIds, exact, goNodes);
    	else
    		filteredIds = getFilteredAllMatch(allProteinIds, exact, goNodes);
    	
        return filteredIds;
    }

    // Returns a list of proteinIds that match any one of the given GO terms
	private List<Integer> getFilteredAnyMatch(List<Integer> allProteinIds,
			boolean exact, List<GONode> goNodes) throws SQLException {
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		
    		for(GONode node: goNodes) {
    			Annotation annot = GoTermSearcher.isProteinAnnotated(protein.getNrseqProteinId(), node.getId());
    			
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
			boolean exact, List<GONode> goNodes) throws SQLException {
		
		List<Integer> filteredIds = new ArrayList<Integer>();
    	ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        
    	for(Integer proteinId: allProteinIds) {
    		ProteinferProtein protein = protDao.loadProtein(proteinId);
    		
    		boolean matchAll = true;
    		
    		for(GONode node: goNodes) {
    			Annotation annot = GoTermSearcher.isProteinAnnotated(protein.getNrseqProteinId(), node.getId());
    			
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
