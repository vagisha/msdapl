package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;

public class GenericIdPickerProtein <T extends GenericIdPickerPeptide<?,?>> extends GenericProteinferProtein<T>{

    private int clusterId = -1;
    private int groupId = -1;
    private boolean isParsimonious;
    private double nsaf = -1.0; // normalized spectrum abundance factor
    
    
    public double getNsaf() {
        return nsaf;
    }
    public void setNsaf(double nsaf) {
        this.nsaf = nsaf;
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
        for(GenericIdPickerPeptide<?,?> pept: this.getPeptides()) {
            if(pept.getGroupId() == peptideGrpId)
                return true;
        }
        return false;
    }
    
}

