package edu.uwpr.protinfer.fdr;

import java.util.Comparator;

public class PeptideSequenceMatch {

    private double score;
    private double fdr = 1.0;
    
    public void setMatchScore(double score) {
        this.score = score;
    }
    
    public double getMatchScore() {
        return this.score;
    }
    
    public void setFdr(double fdr) {
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
        return "Score: "+score+"; FDR: "+fdr;
    }
}
