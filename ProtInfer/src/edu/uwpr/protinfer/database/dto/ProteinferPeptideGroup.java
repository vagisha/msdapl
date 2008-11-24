package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class ProteinferPeptideGroup {

    private final int groupId;
    private List<ProteinferPeptide> peptides;
    private List<Integer> matchingProteinGroupIds;
    
    public ProteinferPeptideGroup(int groupId) {
        this.groupId = groupId;
        peptides = new ArrayList<ProteinferPeptide>();
        matchingProteinGroupIds = new ArrayList<Integer>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public void setPeptides(List<ProteinferPeptide> peptides) {
        if(peptides != null)
            this.peptides = peptides;
    }
    
    public List<ProteinferPeptide> getPeptides() {
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
        for(ProteinferPeptide pept: peptides) {
            cnt += pept.getSpectralCount();
        }
        return cnt;
    }
}
