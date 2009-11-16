/**
 * ProteinAccessionFilter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.www.compare.util.FastaProteinLookupUtil;

/**
 * 
 */
public class ProteinAccessionFilter {

    private static final ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    
    private ProteinAccessionFilter() {}
    
    public static List<Integer> filterByProteinAccession(int pinferId,
            List<Integer> allProteinIds, String accessionLike) {
        
        // get the protein accession map if we have one
        // but don't create a new one if it does not exit
        // the protein inference we are looking at right now. 
        Map<Integer, String> proteinAccessionMap = ProteinAccessionStore.getInstance().getAccessionMapForProteinInference(pinferId, false);
        
        return filterByProteinAccession(pinferId, allProteinIds, proteinAccessionMap, accessionLike);
    }
    
    public static List<Integer> filterByProteinAccession(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, String> proteinAccessionMap, String accessionLike) {
        
        Set<String> reqAcc = new HashSet<String>();
        String[] tokens = accessionLike.split(",");
        for(String tok: tokens)
            reqAcc.add(tok.trim().toLowerCase());
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        
        // If we have a accession map look in there
        if(proteinAccessionMap != null) {
        
            for(int id: allProteinIds) {
                String acc = proteinAccessionMap.get(id);
                if(acc != null) acc = acc.toLowerCase();
                // first check if the exact accession is given to us
                if(reqAcc.contains(acc)) {
                    filtered.add(id);
                    continue;
                }
                // we may have a partial accession
                for(String ra: reqAcc) {
                    if(acc.contains(ra)) {
                        filtered.add(id);
                        break;
                    }
                }
            }
        }
        
        // Look in the database for matching ids.
        else {
            List<Integer> found = FastaProteinLookupUtil.getInstance().getProteinIds(new ArrayList<String>(reqAcc), pinferId);
            
            // get the corresponding protein inference protein ids
            if(found.size() > 0) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(found));
                Collections.sort(piProteinIds);
                for(int id: allProteinIds) {
                    if(Collections.binarySearch(piProteinIds, id) >= 0)
                        filtered.add(id);
                }
            }
        }
        
        return filtered;
    }
}
