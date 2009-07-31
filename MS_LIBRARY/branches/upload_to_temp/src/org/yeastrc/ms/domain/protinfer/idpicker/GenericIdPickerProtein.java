package org.yeastrc.ms.domain.protinfer.idpicker;

import java.text.DecimalFormat;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

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
    public String getNsafFormatted() {
        String format = "0.000000";
        DecimalFormat df = new DecimalFormat(format);
        return df.format(nsaf);
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

