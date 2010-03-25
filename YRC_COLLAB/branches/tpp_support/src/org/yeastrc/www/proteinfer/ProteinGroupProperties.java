/**
 * ProteinGroupProperties.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

class ProteinGroupProperties {
    
    private List<ProteinProperties> proteinPropsList;
    private SORT_ORDER sortOrder;
    
    ProteinGroupProperties(SORT_ORDER sortOrder) {
        proteinPropsList = new ArrayList<ProteinProperties>();
        this.sortOrder = sortOrder;
    }
    
    void add(ProteinProperties props) {
        this.proteinPropsList.add(props);
    }
    
    double getGroupMolecularWt() {
        return getSortedByMolWt().get(0).getMolecularWt();
    }
    
    double getGroupPi() {
        return getSortedByPi().get(0).getPi();
    }
    
    String getGroupAccession() {
    	return getSortedByAccession().get(0).getAccession(sortOrder);
    }
    
    int getProteinGroupId() {
        return proteinPropsList.get(0).getProteinGroupId();
    }
    
    List<ProteinProperties> getSortedByMolWt() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o2.getMolecularWt()).compareTo(o1.getMolecularWt());
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o1.getMolecularWt()).compareTo(o2.getMolecularWt());
    			}});
    	}
        return proteinPropsList;
    }
    
    List<ProteinProperties> getSortedByPi() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o2.getPi()).compareTo(o1.getPi());
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o1.getPi()).compareTo(o2.getPi());
    			}});
    	}
        return proteinPropsList;
    }
    
    List<ProteinProperties> getSortedByAccession() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return o2.getAccession(sortOrder).compareTo(o1.getAccession(sortOrder));
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return o1.getAccession(sortOrder).compareTo(o2.getAccession(sortOrder));
    			}});
    	}
        return proteinPropsList;
    }
}