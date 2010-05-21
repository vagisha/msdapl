package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;

public class IdPickerPeptide extends BaseProteinferPeptide<IdPickerSpectrumMatch> {

    private int groupId = -1;
    
    public IdPickerPeptide() {
        super();
    }
    
    public IdPickerPeptide(int peptideGroupId) {
        super();
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
    
    public IdPickerSpectrumMatch getBestSpectrumMatch() {
        IdPickerSpectrumMatch bestPsm = null;
        for(IdPickerSpectrumMatch psm: this.getSpectrumMatchList()) {
            if(bestPsm == null) {
                bestPsm = psm;
            }
            else {
                bestPsm = bestPsm.getFdr() < psm.getFdr() ? bestPsm : psm;
            }
        }
        return bestPsm;
    }
}
