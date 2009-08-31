/**
 * ProteinAccessionSorter.java
 * @author Vagisha Sharma
 * Aug 29, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ProteinAccessionSorter {

    private ProteinAccessionSorter() {}
    
    public static List<Integer> sortIdsByAccession(List<Integer> proteinIds, int pinferId) {
        
        Map<Integer, String> proteinAccessionMap = ProteinAccessionStore.getInstance().getAccessionMapForProteinInference(pinferId);
        return sortIdsByAccession(proteinIds, proteinAccessionMap);
    }

    public static List<Integer> sortIdsByAccession(List<Integer> proteinIds, Map<Integer, String> proteinAccessionMap) {
        
        List<ProteinIdAccession> accMap = new ArrayList<ProteinIdAccession>(proteinIds.size());
        
        for(int id: proteinIds) {
            accMap.add(new ProteinIdAccession(id, proteinAccessionMap.get(id)));
        }
        Collections.sort(accMap, new Comparator<ProteinIdAccession>() {
            public int compare(ProteinIdAccession o1, ProteinIdAccession o2) {
                return o1.accession.toLowerCase().compareTo(o2.accession.toLowerCase());
            }});
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        for(ProteinIdAccession pa: accMap) {
            sortedIds.add(pa.proteinId);
        }
        return sortedIds;
    }
    
    
    
    private static class ProteinIdAccession {
        int proteinId;
        String accession;
        public ProteinIdAccession(int proteinId, String accession) {
            this.proteinId = proteinId;
            this.accession = accession;
        }
    }
}
