package edu.uwpr.protinfer;

import java.util.Comparator;

public class PeptideSequenceMatch {

    private SearchSource source;
    private int scanNumber;
    private int assumedCharge;
    private double score;
    
    private int scanId; // could be the database id of the scan
    private int hitId; // could be the database id of this search result.
    
    private FilterScore filterScore;
    
    public PeptideSequenceMatch(SearchSource source, int scanNumber, int charge, double score) {
        this.source = source;
        this.assumedCharge = charge;
        this.scanNumber = scanNumber;
        this.score = score;
    }
    
    public double getScore() {
        return this.score;
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
    
    public static final class PsmComparator implements Comparator<PeptideSequenceMatch> {
        @Override
        public int compare(PeptideSequenceMatch o1, PeptideSequenceMatch o2) {
            return Double.valueOf(o1.getScore()).compareTo(Double.valueOf(o2.getScore()));
        }
    }
    
    public void setFilterScore(FilterScore filterScore) {
        this.filterScore = filterScore;
    }
    
    public FilterScore getFilterScore() {
        return filterScore;
    }
    
    public boolean accepted() {
        return filterScore.pass();
    }
    
    public String toString() {
        return "Source: "+source.getFileName()+"; Scan: "+scanNumber+"; charge: "+assumedCharge+"; score: "+score;
    }
}
