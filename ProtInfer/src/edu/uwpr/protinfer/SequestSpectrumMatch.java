package edu.uwpr.protinfer;

import java.math.BigDecimal;

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
    
    public double getXcorrRounded() {
        return round(xcorr.doubleValue());
    }
    
    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }
    
    public BigDecimal getDeltaCn() {
        return deltaCn;
    }
    
    public double getDeltaCnRounded() {
        return round(deltaCn.doubleValue());
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
    
    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
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
    
    public double getFdrRounded() {
        return round(fdr);
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
    
    private double round(double toRound) {
        return Math.round((toRound*10000.0))/(10000.0);
    }
}
