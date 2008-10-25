package edu.uwpr.protinfer.filter.fdr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class SearchHit implements FdrCandidate{

    private double score;
    private boolean decoy;
    private double fdr = 1.0;
    
    public SearchHit(double score, boolean decoy) {
        this.score = score;
        this.decoy = decoy;
    }
    
    @Override
    public double getFdr() {
        return fdr;
    }

    @Override
    public boolean isDecoy() {
        return decoy;
    }

    @Override
    public boolean isTarget() {
        return !decoy;
    }

    @Override
    public void setFdr(double fdr) {
        this.fdr = fdr;
    }
    
    public static void main(String[] args) throws FdrCalculatorException {
        Random random = new Random();
        List<SearchHit> hits = new ArrayList<SearchHit>();
        for (int i = 0; i < 100; i++) {
            hits.add(new SearchHit(random.nextInt(10), true)); // add a docoy hit
            hits.add(new SearchHit(random.nextInt(10), false)); // add a target hit
        }
        
        FdrCalculator calculator = new FdrCalculator();
        calculator.calculateFdr(hits, new Comparator<SearchHit>() {

            @Override
            public int compare(SearchHit o1, SearchHit o2) {
                return Double.valueOf(o1.score).compareTo(Double.valueOf(o2.score));
            }});
        
    }
}
