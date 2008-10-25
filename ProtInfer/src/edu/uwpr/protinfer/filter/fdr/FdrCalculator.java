package edu.uwpr.protinfer.filter.fdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class FdrCalculator <T extends FdrCandidate> {

    private static final Logger log = Logger.getLogger(FdrCalculator.class);
    
    protected abstract boolean considerCandidate(T candidate);
    
    protected abstract double calculateFdr(int targetCount, int decoyCount);
    
    public void calculateFdr(List<T> candidates, Comparator<T> comparator) {
        
        List<T> myCandidateList = new ArrayList<T>(candidates.size());
        
        int targetCount = 0;
        int decoyCount = 0;
        for(T candidate: candidates) {
            if (considerCandidate(candidate)) { // will we consider this candidate for FDR calculation?
                                                // e.g we may not want to use candidates that are both target and decoy.
                myCandidateList.add(candidate); 
                if (candidate.isTarget())
                    targetCount++;
                else 
                    decoyCount++;
            }
        }
        
        if (decoyCount == 0) {
            log.warn("No decoy hits found for calculating FDR!");
        }
        
        log.info("Calculating FDR for "+targetCount+" target candidates and "+decoyCount+" decoy candidates");
        
        if (decoyCount == 0) {
            for (T candidate: myCandidateList)
                candidate.setFdr(0.0);
            return;
        }
        
        // sort -- highest scoring candidates at the top
        Collections.sort(myCandidateList, Collections.reverseOrder(comparator));
        
        targetCount = 0;
        decoyCount = 0;
        for (T candidate: myCandidateList) {
            if (candidate.isTarget())
                targetCount++;
            else
                decoyCount++;
            if (candidate.isTarget()) {
                double fdr = calculateFdr(targetCount, decoyCount);
                candidate.setFdr(fdr);
            }
        }
    }
}
