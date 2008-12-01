package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class IdPickerSpectrumMatch extends ProteinferSpectrumMatch {

    private double fdr = -1;
    
    public IdPickerSpectrumMatch() {
        super();
    }
    
    public IdPickerSpectrumMatch(int pinferPeptideId, int msRunSearchResultId, double fdr) {
        super(pinferPeptideId, msRunSearchResultId);
        this.fdr = fdr;
    }
    
    public double getFdr() {
        return fdr;
    }
    
    public double getFdrRounded() {
        return Math.round(fdr * 1000.0) / 1000.0;
    }
    
    public void setFdr(double fdr) {
        this.fdr = fdr;
    }
}
