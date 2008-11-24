package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class ProteinferCluster {

    private int pinferId;
    private int clusterId;
    private List<ProteinferProteinGroup> proteinGroups;
    private List<ProteinferPeptideGroup> peptideGroups;
    
    public ProteinferCluster(int pinferId, int clusterId) {
        this.pinferId = pinferId;
        this.clusterId = clusterId;
        proteinGroups = new ArrayList<ProteinferProteinGroup>();
        peptideGroups = new ArrayList<ProteinferPeptideGroup>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getClusterId() {
        return clusterId;
    }
    
    public List<ProteinferPeptideGroup> getPeptideGroups() {
        return peptideGroups;
    }

    public List<ProteinferProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public void setProteinGroups(List<ProteinferProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
    
    public void addProteinGroup(ProteinferProteinGroup group) {
        proteinGroups.add(group);
    }

    public void setPeptideGroups(List<ProteinferPeptideGroup> peptideGroups) {
        this.peptideGroups = peptideGroups;
    }
    
    public void addPeptideGroup(ProteinferPeptideGroup group) {
        this.peptideGroups.add(group);
    }
    
    public boolean proteinAndPeptideGroupsMatch(int protGrpId, int peptGrpId) {
        for(ProteinferPeptideGroup peptGrp: peptideGroups) {
            if(peptGrp.getGroupId() == peptGrpId) {
                return peptGrp.matchesProteinGroup(protGrpId);
            }
        }
        return false;
    }
    
}
