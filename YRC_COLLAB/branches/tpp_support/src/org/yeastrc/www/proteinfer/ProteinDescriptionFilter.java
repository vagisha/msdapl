/**
 * ProteinDescriptionFilter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.nrseq.FastaProteinLookupUtil;

/**
 * 
 */
public class ProteinDescriptionFilter {

    private ProteinferProteinDAO protDao = null;
    
    private static ProteinDescriptionFilter instance = null;
    
    private ProteinDescriptionFilter() {
    	protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    }
    
    public static ProteinDescriptionFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinDescriptionFilter();
    	return instance;
    }
    
    
    public List<Integer> filterForProtInferByProteinDescription(int pinferId,
            List<Integer> proteinIds, String descriptionLike, boolean includeMatching) {
        
    	Set<String> reqDescriptions = new HashSet<String>();
        String[] tokens = descriptionLike.split(",");
        for(String tok: tokens)
            // In MySQL string comparisons are NOT case sensitive unless one of the operands is a binary string
            reqDescriptions.add(tok.trim()); 
        
        // First get the NRSEQ protein IDs that match the description terms
        List<Integer> found = FastaProteinLookupUtil.getInstance().getProteinIdsForDescriptions(new ArrayList<String>(reqDescriptions), pinferId);
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        if(found.size() > 0) {
            // Get the protein inference IDs corresponding to the matching NRSEQ IDs.
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(found));
            Collections.sort(piProteinIds);
            for(int id: proteinIds) {
                int contains = Collections.binarySearch(piProteinIds, id);
                if(includeMatching && contains >= 0)
                    filtered.add(id);
                else if(!includeMatching && contains < 0)
                    filtered.add(id);
            }
        }
        
        return filtered;
    }
    
    public List<Integer> filterNrseqIdsByDescription(List<Integer> allNrseqIds, 
    		List<Integer> fastaDatabaseIds, String searchString, boolean includeMatching) throws SQLException {

        List<Integer> sortedIds = getSortedNrseqIdsMatchingDescription(fastaDatabaseIds,searchString);
        if(sortedIds == null || sortedIds.size() == 0) {
        	if(includeMatching)
        		return new ArrayList<Integer>(0); // no matching nrseq IDs found
        	else
        		return allNrseqIds;
        }

        // Remove the ones that match
        if(includeMatching)
        	return getMatching(allNrseqIds, sortedIds);
        else
        	return getNotMatching(allNrseqIds, sortedIds);
    }
    
    private List<Integer> getSortedNrseqIdsMatchingDescription(List<Integer> fastaDatabaseIds, String searchString) {
        if(searchString == null || searchString.trim().length() == 0)
            return null;

        // get the protein ids for the descriptions the user is searching for
        Set<Integer> proteinIds = new HashSet<Integer>();
        String tokens[] = searchString.split(",");

        for(String token: tokens) {
            String description = token.trim();
            if(description.length() > 0) {
                List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteinsForDescription(fastaDatabaseIds, description);
                for(NrDbProtein protein: proteins)
                    proteinIds.add(protein.getProteinId());
            }
        }

        // sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        return sortedIds;
    }
    
    private List<Integer> getMatching(List<Integer> allNrseqIds, List<Integer> sortedMatchingIds) {
    	
    	List<Integer> matching = new ArrayList<Integer>();
        for(int nrseqId: allNrseqIds) {
            if(Collections.binarySearch(sortedMatchingIds, nrseqId) >= 0)
                matching.add(nrseqId);
        }
        return matching;
    }
    
    private List<Integer> getNotMatching(List<Integer> allNrseqIds, List<Integer> sortedMatchingIds) {
    	
    	List<Integer> nonMatching = new ArrayList<Integer>();
        for(int nrseqId: allNrseqIds) {
            if(Collections.binarySearch(sortedMatchingIds, nrseqId) < 0)
                nonMatching.add(nrseqId);
        }
        return nonMatching;
    }
    
}
