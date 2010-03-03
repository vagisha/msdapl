/**
 * ProteinDescriptionFilter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.www.compare.util.FastaProteinLookupUtil;

/**
 * 
 */
public class ProteinDescriptionFilter {

    private static final ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    
    private ProteinDescriptionFilter() {}
    
    
    public static List<Integer> filterByProteinDescription(int pinferId,
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
    
    private static class NrDbProtComparator implements Comparator<NrDbProtein> {
        public int compare(NrDbProtein o1, NrDbProtein o2) {
            return Integer.valueOf(o1.getProteinId()).compareTo(o2.getProteinId());
        }
    }
    
}
