package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.List;

public class IdPickerPeptideGroup {

    private final int groupId;
    private List<IdPickerPeptide> peptides;
    private List<Integer> matchingProteinGroupIds;
    
    public IdPickerPeptideGroup(int groupId) {
        this.groupId = groupId;
        peptides = new ArrayList<IdPickerPeptide>();
        matchingProteinGroupIds = new ArrayList<Integer>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public void setPeptides(List<IdPickerPeptide> peptides) {
        if(peptides != null)
            this.peptides = peptides;
    }
    
    public List<IdPickerPeptide> getPeptides() {
        return this.peptides;
    }
    
    public int getPeptideCount() {
        return peptides.size();
    }
    
    public List<Integer> getMatchingProteinGroupIds() {
        return matchingProteinGroupIds;
    }
    
    public void setMatchingProteinGroupIds(List<Integer> protGrpIds) {
        this.matchingProteinGroupIds = protGrpIds;
    }
    
    public boolean matchesProteinGroup(int protGrpId) {
        return matchingProteinGroupIds.contains(protGrpId);
    }
    
    public boolean isUniqueToProteinGroup() {
        return matchingProteinGroupIds.size() == 1;
    }
    
    public int getSpectrumCount() {
        int cnt = 0;
        for(IdPickerPeptide pept: peptides) {
            cnt += pept.getSpectralCount();
        }
        return cnt;
    }
}
