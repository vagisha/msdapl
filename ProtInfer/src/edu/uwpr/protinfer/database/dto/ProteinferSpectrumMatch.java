package edu.uwpr.protinfer.database.dto;

public class ProteinferSpectrumMatch {

    private int proteinferPeptideId;
    private double fdr = -1;
    private int msRunSearchResultId;
    
    public ProteinferSpectrumMatch() {}
    
    public ProteinferSpectrumMatch(int pinferPeptideId, int msRunSearchResultId) {
        this.proteinferPeptideId = pinferPeptideId;
        this.msRunSearchResultId = msRunSearchResultId;
    }
    
    public ProteinferSpectrumMatch(int pinferPeptideId, int msRunSearchResultId, double fdr) {
        this(pinferPeptideId, msRunSearchResultId);
        this.fdr = fdr;
    }
    
    public int getProteinferPeptideId() {
        return proteinferPeptideId;
    }
    
    public void setProteinferPeptideId(int pinferPeptideId) {
        this.proteinferPeptideId = pinferPeptideId;
    }
    
    public double getFdr() {
        return fdr;
    }
    
    public void setFdr(double fdr) {
        this.fdr = fdr;
    }
    
    public int getMsRunSearchResultId() {
        return msRunSearchResultId;
    }
    
    public void setMsRunSearchResultId(int msRunSearchResultId) {
        this.msRunSearchResultId = msRunSearchResultId;
    }
    
}
