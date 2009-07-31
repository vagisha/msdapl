package org.yeastrc.ms.domain.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;


public class IdPickerCluster {

    private int pinferId;
    private int clusterId;
    private List<IdPickerProteinGroup> proteinGroups;
    private List<IdPickerPeptideGroup> peptideGroups;
    
    public IdPickerCluster(int pinferId, int clusterId) {
        this.pinferId = pinferId;
        this.clusterId = clusterId;
        proteinGroups = new ArrayList<IdPickerProteinGroup>();
        peptideGroups = new ArrayList<IdPickerPeptideGroup>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getClusterId() {
        return clusterId;
    }
    
    public List<IdPickerPeptideGroup> getPeptideGroups() {
        return peptideGroups;
    }

    public List<IdPickerProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public void setProteinGroups(List<IdPickerProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
    
    public void addProteinGroup(IdPickerProteinGroup group) {
        proteinGroups.add(group);
    }

    public void setPeptideGroups(List<IdPickerPeptideGroup> peptideGroups) {
        this.peptideGroups = peptideGroups;
    }
    
    public void addPeptideGroup(IdPickerPeptideGroup group) {
        this.peptideGroups.add(group);
    }
    
    public boolean proteinAndPeptideGroupsMatch(int protGrpId, int peptGrpId) {
        for(IdPickerPeptideGroup peptGrp: peptideGroups) {
            if(peptGrp.getGroupId() == peptGrpId) {
                return peptGrp.matchesProteinGroup(protGrpId);
            }
        }
        return false;
    }
    
}
