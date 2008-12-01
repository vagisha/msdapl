package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;

public class IdPickerPeptide extends BaseProteinferPeptide<IdPickerSpectrumMatch> {

    private int groupId;
    
    public IdPickerPeptide() {
        super();
    }
    
    public IdPickerPeptide(int pinferId, int peptideGroupId) {
        super(pinferId);
        this.groupId = peptideGroupId;
    }
    
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public float getBestFdr() {
        float best = Float.MAX_VALUE;
        for(IdPickerSpectrumMatch psm: this.getSpectrumMatchList()) {
            best = (float) Math.min(best, psm.getFdr());
        }
        return (float) (Math.round(best*100.0) / 100.0);
    }
}
