package edu.uwpr.protinfer;

import java.math.BigDecimal;

import edu.uwpr.protinfer.filter.fdr.FdrFilterable;
import edu.uwpr.protinfer.idpicker.FdrCandidateHasCharge;
import edu.uwpr.protinfer.infer.SearchSource;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class SequestSpectrumMatch implements SpectrumMatch {

    private SearchSource source;
    private int scanNumber;
    private int assumedCharge;
    private BigDecimal xcorr;
    private BigDecimal deltaCn;
    
    private String peptideSequence;
    
    private double fdr = 1.0;
    private boolean accepted;
    
    private int scanId; // could be the database id of the scan
    private int hitId;  // could be the database id of this search result.
    
    
    public SequestSpectrumMatch(SearchSource source, int scanNumber, int charge, String peptideSequence) {
        this.source = source;
        this.assumedCharge = charge;
        this.scanNumber = scanNumber;
        this.peptideSequence = peptideSequence;
    }
    
    public BigDecimal getXcorr() {
        return this.xcorr;
    }
    
    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }
    
    public BigDecimal getDeltaCn() {
        return deltaCn;
    }

    public void setDeltaCn(BigDecimal deltaCn) {
        this.deltaCn = deltaCn;
    }
    
    public int getCharge() {
        return assumedCharge;
    }
    
    public int getScanNumber() {
        return scanNumber;
    }
    
    public SearchSource getSearchSource() {
        return source;
    }
    
    public int getScanId() {
        return scanId;
    }

    public void setScanId(int scanId) {
        this.scanId = scanId;
    }

    public int getHitId() {
        return hitId;
    }

    public void setHitId(int hitId) {
        this.hitId = hitId;
    }
    
    public String toString() {
        return "Source: "+source.getFileName()+"; Scan: "+scanNumber+"; charge: "+assumedCharge
            +"; seq: "+peptideSequence
            +"; xcorr: "+"; deltaCN: "+deltaCn+"; fdr: "+fdr+"; accepted: "+accepted;
    }

    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
    }
    
    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getPeptideSequence() {
        return this.peptideSequence;
    }
}
