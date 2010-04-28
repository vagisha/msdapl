/**
 * WIdPickerPeptideGroup.java
 * @author Vagisha Sharma
 * Jan 21, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;

/**
 * 
 */
public class WIdPickerPeptideGroup {

    private final int pinferId;
    private final int groupId;
    private List<? extends IdPickerPeptideBase> peptides;
    private Set<Integer> matchingProteinGroupIds;
    
    public WIdPickerPeptideGroup(List<? extends IdPickerPeptideBase> groupPeptides) {
        if(groupPeptides.size() > 0) {
            this.pinferId = groupPeptides.get(0).getProteinferId();
            this.groupId = groupPeptides.get(0).getGroupId();
        }
        else {
            pinferId = 0;
            groupId = 0;
        }
        this.peptides = groupPeptides;
        matchingProteinGroupIds = new HashSet<Integer>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public List<? extends IdPickerPeptideBase> getPeptides() {
        return this.peptides;
    }
    
    public int getPeptideCount() {
        return peptides.size();
    }
    
    public List<Integer> getMatchingProteinGroupIds() {
        return new ArrayList<Integer>(matchingProteinGroupIds);
    }
    
    public void addMatchingProteinGroupId(int protGrpId) {
        this.matchingProteinGroupIds.add(protGrpId);
    }
    
    public boolean matchesProteinGroup(int protGrpId) {
        return matchingProteinGroupIds.contains(protGrpId);
    }
    
    public boolean isUniqueToProteinGroup() {
        return matchingProteinGroupIds.size() == 1;
    }
}
