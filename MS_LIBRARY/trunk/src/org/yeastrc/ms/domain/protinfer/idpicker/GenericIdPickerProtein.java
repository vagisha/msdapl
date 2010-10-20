package org.yeastrc.ms.domain.protinfer.idpicker;

import java.text.DecimalFormat;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

public class GenericIdPickerProtein <T extends GenericIdPickerPeptide<?,?>> extends GenericProteinferProtein<T>{

    private int clusterId = -1;
    private int groupId = -1;
    private boolean isParsimonious;
    private boolean isSubset;
    private double nsaf = -1.0; // normalized spectrum abundance factor
    private static final DecimalFormat df = new DecimalFormat("0.000000");
    
    public double getNsaf() {
        return nsaf;
    }
    public void setNsaf(double nsaf) {
        this.nsaf = nsaf;
    }
    public String getNsafFormatted() {
    	if(isParsimonious)
    		return df.format(nsaf);
    	else
    		return "-1";
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
    
    public boolean getIsSubset() {
		return isSubset;
	}
	public void setIsSubset(boolean isSubset) {
		this.isSubset = isSubset;
	}
	
	public boolean matchesPeptideGroup(int peptideGrpId) {
        for(GenericIdPickerPeptide<?,?> pept: this.getPeptides()) {
            if(pept.getGroupId() == peptideGrpId)
                return true;
        }
        return false;
    }
    
}

