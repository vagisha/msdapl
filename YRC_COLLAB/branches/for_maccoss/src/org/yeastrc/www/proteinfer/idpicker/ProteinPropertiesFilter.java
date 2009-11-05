/**
 * ProteinPropertiesFilter.java
 * @author Vagisha Sharma
 * Nov 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ProteinPropertiesFilter {

    private ProteinPropertiesFilter() {}
    
    public static List<Integer> filterByMolecularWt(int pinferId,
            List<Integer> allProteinIds, double minWt, double maxWt) {
        
        // get a map of the protein ids and protein properties
        Map<Integer, ProteinProperties> propsMap = ProteinPropertiesStore.getInstance().getPropertiesMapForProteinInference(pinferId);
        return filterByMolecularWt(pinferId, allProteinIds, propsMap, minWt, maxWt);
    }
    
    private static List<Integer> filterByMolecularWt(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, ProteinProperties> proteinPropertiesMap, double minWt, double maxWt) {
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        
        //  map look in there
        if(proteinPropertiesMap != null) {
        
            for(int id: allProteinIds) {
                ProteinProperties props = proteinPropertiesMap.get(id);
                if(props.getMolecularWt() >= minWt && props.getMolecularWt() <= maxWt)
                    filtered.add(id);
            }
        }
        return filtered;
    }
    
    public static List<Integer> filterByPi(int pinferId,
            List<Integer> allProteinIds, double minPi, double maxPi) {
        
        // get a map of the protein ids and protein properties
        Map<Integer, ProteinProperties> propsMap = ProteinPropertiesStore.getInstance().getPropertiesMapForProteinInference(pinferId);
        return filterByPi(pinferId, allProteinIds, propsMap, minPi, maxPi);
    }
    
    private static List<Integer> filterByPi(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, ProteinProperties> proteinPropertiesMap, double minPi, double maxPi) {
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        //  map look in there
        if(proteinPropertiesMap != null) {
        
            for(int id: allProteinIds) {
                ProteinProperties props = proteinPropertiesMap.get(id);
                if(props.getPi() >= minPi && props.getPi() <= maxPi)
                    filtered.add(id);
            }
        }
        return filtered;
    }
}
