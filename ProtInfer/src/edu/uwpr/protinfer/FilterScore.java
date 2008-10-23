package edu.uwpr.protinfer;

public class FilterScore {

    private String filterDescription;
    private double score;
    private boolean pass;
    
    public FilterScore(String filterDescription, double score, boolean pass) {
        this.filterDescription = filterDescription;
        this.score = score;
        this.pass = pass;
    }

    public String getFilterDescription() {
        return filterDescription;
    }

    public double getScore() {
        return score;
    }
    
    public boolean pass() {
        return pass;
    }
}
