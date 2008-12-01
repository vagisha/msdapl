package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;

public class IdPickerProtein extends BaseProteinferProtein<IdPickerSpectrumMatch, IdPickerPeptide> {

    private int clusterId;
    private int groupId;
    private boolean isParsimonious;
    
    public IdPickerProtein() {
        super();
    }
    
    public int getClusterId() {
        return clusterId;
    }
    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
    
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public boolean getIsParsimonious() {
        return this.isParsimonious;
    }
    public void setIsParsimonious(boolean isParsimonious) {
        this.isParsimonious = isParsimonious;
    }
    
    public boolean matchesPeptideGroup(int peptideGrpId) {
        for(IdPickerPeptide pept: this.getPeptides()) {
            if(pept.getGroupId() == peptideGrpId)
                return true;
        }
        return false;
    }
}
