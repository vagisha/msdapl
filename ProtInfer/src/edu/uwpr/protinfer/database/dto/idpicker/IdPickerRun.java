package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.BaseProteinferRun;

public class IdPickerRun extends BaseProteinferRun<IdPickerInputSummary> {

    private int numUnfilteredProteins = -1;
    private int numUnfilteredPeptides = -1;
    
    public IdPickerRun() {
        super();
    }
    
    public int getNumUnfilteredProteins() {
        return numUnfilteredProteins;
    }
    public void setNumUnfilteredProteins(int numUnfilteredProteins) {
        this.numUnfilteredProteins = numUnfilteredProteins;
    }
    public int getNumUnfilteredPeptides() {
        return numUnfilteredPeptides;
    }
    public void setNumUnfilteredPeptides(int numUnfilteredPeptides) {
        this.numUnfilteredPeptides = numUnfilteredPeptides;
    }
}
