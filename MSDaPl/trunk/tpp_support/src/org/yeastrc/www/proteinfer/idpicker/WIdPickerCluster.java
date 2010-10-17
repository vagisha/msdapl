package org.yeastrc.www.proteinfer.idpicker;

import java.util.List;

public class WIdPickerCluster {

    private int pinferId;
    private int clusterId;
    private List<WIdPickerProteinGroup> proteinGroups;
    private List<WIdPickerPeptideGroup> peptideGroups;
    
    public WIdPickerCluster(int pinferId, int clusterId) {
        this.pinferId = pinferId;
        this.clusterId = clusterId;
    }
    
    public void setProteinGroups(List<WIdPickerProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
    
    public void setPeptideGroups(List<WIdPickerPeptideGroup> peptideGroups) {
        this.peptideGroups = peptideGroups;
    }

    public int getPinferId() {
        return pinferId;
    }

    public void setPinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public List<WIdPickerProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public List<WIdPickerPeptideGroup> getPeptideGroups() {
        return peptideGroups;
    }
    
    public boolean proteinAndPeptideGroupsMatch(int protGrpId, int peptGrpId) {
        for(WIdPickerPeptideGroup peptGrp: peptideGroups) {
            if(peptGrp.getGroupId() == peptGrpId) {
                return peptGrp.matchesProteinGroup(protGrpId);
            }
        }
        return false;
    }
    
}
