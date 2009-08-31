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
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class ProteinDescriptionFilter {

    private static final ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    
    private ProteinDescriptionFilter() {}
    
    
    public static List<Integer> filterByProteinDescription(int pinferId,
            List<Integer> proteinIds, String descriptionLike) {
        
        List<Integer> searchDbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        List<NrDbProtein> nrProteins = NrSeqLookupUtil.getDbProteinsForDescription(searchDbIds, descriptionLike);
        
        NrDbProtComparator comparator = new NrDbProtComparator();
        Collections.sort(nrProteins, comparator);
           
        List<ProteinferProtein> proteins = protDao.loadProteins(pinferId);
        Set<Integer> accepted = new HashSet<Integer>();
        for(ProteinferProtein protein: proteins) {
            NrDbProtein nrp = new NrDbProtein();
            nrp.setProteinId(protein.getNrseqProteinId());
            int idx = Collections.binarySearch(nrProteins, nrp, comparator);
            if(idx >= 0) {
                accepted.add(protein.getId());
            }
        }
        
        List<Integer> acceptedProteinIds = new ArrayList<Integer>(accepted.size());
        for(int id: proteinIds) {
            if(accepted.contains(id))
                acceptedProteinIds.add(id);
        }
        return acceptedProteinIds;
    }
    
    private static class NrDbProtComparator implements Comparator<NrDbProtein> {
        public int compare(NrDbProtein o1, NrDbProtein o2) {
            return Integer.valueOf(o1.getProteinId()).compareTo(o2.getProteinId());
        }
    }
    
}
