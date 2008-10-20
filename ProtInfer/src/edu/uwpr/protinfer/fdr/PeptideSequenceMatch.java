package edu.uwpr.protinfer.fdr;

import java.util.Comparator;

import edu.uwpr.protinfer.pepxml.ScanSearchResult;

public class PeptideSequenceMatch {

    private double score;
    private int scanNumber;
    private int charge;
    private double fdr = 1.0;
    private ScanSearchResult scanResult;
    
    public PeptideSequenceMatch(ScanSearchResult scanResult, int scanNumber, int charge) {
        this.scanResult = scanResult;
        this.charge = charge;
        this.scanNumber = scanNumber;
        this.score = scanResult.getTopHit().getXcorr().doubleValue();
    }
    
    public double getMatchScore() {
        return this.score;
    }
    
    public int getMatchCharge() {
        return charge;
    }
    
    public int getScanNumber() {
        return scanNumber;
    }
    
    public ScanSearchResult getScanSearchResult() {
        return scanResult;
    }
    
    public void setFdr(double fdr) {
        if (this.fdr < 1.0) {
            System.out.println("FDR already set!!! "+fdr+"\n\t"+toString());
        }
        this.fdr = fdr;
    }
    
    public double getFdr() {
        return this.fdr;
    }
    
    public static final class PsmComparator implements Comparator<PeptideSequenceMatch> {
        @Override
        public int compare(PeptideSequenceMatch o1, PeptideSequenceMatch o2) {
            return Double.valueOf(o1.getMatchScore()).compareTo(Double.valueOf(o2.getMatchScore()));
        }
    }
    
    public String toString() {
        return "Scan: "+scanNumber+"; charge: "+charge+"; score: "+score+"; FDR: "+fdr;
    }
}
