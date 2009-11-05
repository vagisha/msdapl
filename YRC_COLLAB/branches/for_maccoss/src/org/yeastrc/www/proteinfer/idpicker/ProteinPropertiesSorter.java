/**
 * ProteinPropertiesSorter.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ProteinPropertiesSorter {

    private ProteinPropertiesSorter() {}
    
    // -------------------------------------------------------------------------------------------
    // SORT BY MOLECULAR WT.
    // -------------------------------------------------------------------------------------------
    public static List<Integer> sortIdsByMolecularWt(List<Integer> proteinIds, int pinferId, boolean groupProteins) {
        
        Map<Integer, ProteinProperties> proteinPropertiesMap = ProteinPropertiesStore.getInstance().getPropertiesMapForProteinInference(pinferId);
        return sortIdsByMolecularWt(proteinIds, groupProteins, proteinPropertiesMap);
    }

    private  static List<Integer> sortIdsByMolecularWt(List<Integer> proteinIds, boolean groupProteins,
            Map<Integer, ProteinProperties> proteinPropertiesMap) {
        
        if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProteinProperties> propsList = new ArrayList<ProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add(proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by indistinguishable protein group
        // simply sort by the molecular wt.
        if(!groupProteins) {
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getMolecularWt()).compareTo(o1.getMolecularWt());
                }});
            
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinProperties props: propsList)
                sortedIds.add(props.getPinferProteinId());
            
            return sortedIds;
        }
        // If we are grouping indistinguishable protein groups sort by the max molecular wt. 
        // for a protein group
        else {
            
            // sort by protein group id first
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                @Override
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Integer.valueOf(o1.getProteinGroupId()).compareTo(o2.getProteinGroupId());
                }});
            
            // create the protein groups
            List<ProteinGroupProperties> grpPropsList = new ArrayList<ProteinGroupProperties>();
            ProteinGroupProperties grpProps = null;
            for(ProteinProperties props: propsList) {
                if(grpProps == null || grpProps.getProteinGroupId() != props.getPinferProteinId()) {
                    grpProps = new ProteinGroupProperties();
                    grpProps.add(props);
                    grpPropsList.add(grpProps);
                }
                else {
                    grpProps.add(props);
                }
            }
            
            // sort the protein groups by max molecular wt.
            Collections.sort(grpPropsList, new Comparator<ProteinGroupProperties>() {
                @Override
                public int compare(ProteinGroupProperties o1,
                        ProteinGroupProperties o2) {
                    return Double.valueOf(o2.getMaxMolecularWt()).compareTo(o1.getMaxMolecularWt());
                }});
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinGroupProperties gp: grpPropsList) {
                for(ProteinProperties props: gp.getSortedByMolWt()) 
                    sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
        
    }
    
    // -------------------------------------------------------------------------------------------
    // SORT BY PI.
    // -------------------------------------------------------------------------------------------
    public static List<Integer> sortIdsByPi(List<Integer> proteinIds, int pinferId, boolean groupProteins) {
        
        Map<Integer, ProteinProperties> proteinPropertiesMap = ProteinPropertiesStore.getInstance().getPropertiesMapForProteinInference(pinferId);
        return sortIdsByPi(proteinIds, groupProteins, proteinPropertiesMap);
    }

    private  static List<Integer> sortIdsByPi(List<Integer> proteinIds, boolean groupProteins,
            Map<Integer, ProteinProperties> proteinPropertiesMap) {
        
        if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProteinProperties> propsList = new ArrayList<ProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add(proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by indistinguishable protein group
        // simply sort by the molecular wt.
        if(!groupProteins) {
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getPi()).compareTo(o1.getPi());
                }});
            
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinProperties props: propsList)
                sortedIds.add(props.getPinferProteinId());
            
            return sortedIds;
        }
        // If we are grouping indistinguishable protein groups sort by the max molecular wt. 
        // for a protein group
        else {
            
            // sort by protein group id first
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                @Override
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Integer.valueOf(o1.getProteinGroupId()).compareTo(o2.getProteinGroupId());
                }});
            
            // create the protein groups
            List<ProteinGroupProperties> grpPropsList = new ArrayList<ProteinGroupProperties>();
            ProteinGroupProperties grpProps = null;
            for(ProteinProperties props: propsList) {
                if(grpProps == null || grpProps.getProteinGroupId() != props.getPinferProteinId()) {
                    grpProps = new ProteinGroupProperties();
                    grpProps.add(props);
                    grpPropsList.add(grpProps);
                }
                else {
                    grpProps.add(props);
                }
            }
            
            // sort the protein groups by max molecular wt.
            Collections.sort(grpPropsList, new Comparator<ProteinGroupProperties>() {
                @Override
                public int compare(ProteinGroupProperties o1,
                        ProteinGroupProperties o2) {
                    return Double.valueOf(o2.getMaxPi()).compareTo(o1.getMaxPi());
                }});
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinGroupProperties gp: grpPropsList) {
                for(ProteinProperties props: gp.getSortedByMolWt()) 
                    sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
        
    }
    
    
    private static class ProteinGroupProperties {
        
        private List<ProteinProperties> proteinPropsList;
        private double maxMolWt = 0;
        private double maxPi = 0;
        
        private ProteinGroupProperties() {
            proteinPropsList = new ArrayList<ProteinProperties>();
        }
        
        void add(ProteinProperties props) {
            this.proteinPropsList.add(props);
            maxMolWt = Math.max(maxMolWt, props.getMolecularWt());
            maxPi = Math.max(maxPi, props.getPi());
        }
        
        double getMaxMolecularWt() {
            return maxMolWt;
        }
        
        double getMaxPi() {
            return maxPi;
        }
        
        int getProteinGroupId() {
            return proteinPropsList.get(0).getProteinGroupId();
        }
        
        List<ProteinProperties> getSortedByMolWt() {
            Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
                @Override
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getMolecularWt()).compareTo(o1.getMolecularWt());
                }});
            return proteinPropsList;
        }
        
        List<ProteinProperties> getSortedByPi() {
            Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
                @Override
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getPi()).compareTo(o1.getPi());
                }});
            return proteinPropsList;
        }
    }
}
