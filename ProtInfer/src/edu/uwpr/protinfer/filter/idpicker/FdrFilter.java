package edu.uwpr.protinfer.filter.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.FilterScore;
import edu.uwpr.protinfer.PeptideSequenceMatch;
import edu.uwpr.protinfer.SearchHit;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.SearchHitFilter;

public class FdrFilter implements SearchHitFilter {

    private List<SearchHit> targetMatchList;
    private List<SearchHit> decoyMatchList;
    private Comparator<SearchHit> psmComparator;
    private double thresholdFdr = 0.05;
    private double decoyRatio = 1.0;
    private boolean separateChargeStates = false;
    
    private static final Logger log = Logger.getLogger(FdrFilter.class);

    public FdrFilter() {
        this.targetMatchList = new ArrayList<SearchHit>();
        this.decoyMatchList = new ArrayList<SearchHit>();
    }

    public void setThresholdFdr(double fdr) {
        this.thresholdFdr = fdr;
    }
    
    public void setDecoyRatio(double decoyRatio) {
        this.decoyRatio = decoyRatio;
    }
    
    public void separateChargeStates() {
        this.separateChargeStates = true;
    }
    
    @Override
    public List<SearchHit> filterSearchHits(List<SearchHit> searchHits, Comparator<SearchHit> comparator) throws FilterException {
        
        this.psmComparator = comparator;
        
        for(SearchHit hit: searchHits) {
            if (hit.isDecoyHit() && !hit.isTargetHit())
                addDecoyHit(hit);
            else if (hit.isTargetHit() && !hit.isDecoyHit())
                addTargetHit(hit);
        }
        
        if (decoyMatchList.size() == 0) {
            throw new FilterException("No decoy hits found! Cannot calculate FDR without decoy hits");
        }
        
        calculateFdr();
        return searchHits;
    }
    
    private void addTargetHit(SearchHit hit) {
        targetMatchList.add(hit);
    }

    private void addDecoyHit(SearchHit hit) {
        decoyMatchList.add(hit);
    }

    private void calculateFdr() {
        
        log.info("Calculating FDR for "+targetMatchList.size()+" PSMs");
        
        if (!separateChargeStates) {
            calculateFdr(targetMatchList, decoyMatchList, thresholdFdr);
        }
        
        else {
            List<PeptideSequenceMatch> acceptedPsms = new ArrayList<PeptideSequenceMatch>();
            Collections.sort(targetMatchList, new PsmChargeComparator()); // sort by charge
            Collections.sort(decoyMatchList, new PsmChargeComparator()); // sort by charge
            int currentChg = 1;
            List<SearchHit> forwardList = new ArrayList<SearchHit>();
            List<SearchHit> reverseList = new ArrayList<SearchHit>();
            int fidx = 0;
            int ridx = 0;
            while(true) {
                int nextChg = currentChg;
                for (; fidx < targetMatchList.size(); fidx++) {
                    SearchHit hit = targetMatchList.get(fidx);
                    if (hit.getCharge() != currentChg) {
                        nextChg = hit.getCharge();
                        break;
                    }
                    else {
                        forwardList.add(hit);
                    }
                }
                for (; ridx < decoyMatchList.size(); ridx++) {
                    SearchHit hit = decoyMatchList.get(ridx);
                    if (hit.getCharge() != currentChg) {
                        break;
                    }
                    else
                        reverseList.add(hit);
                }
                System.out.println("Calculating FDR for +"+currentChg+" PSMs");
                calculateFdr(forwardList, reverseList, thresholdFdr);
                
                // have we seen all the forward sequence matches? 
                if (fidx >= targetMatchList.size())
                    break;
                
                currentChg = nextChg;
                forwardList.clear();
                reverseList.clear();
            }
        }
    }

    private static final class PsmChargeComparator implements Comparator<SearchHit> {

        public int compare(SearchHit o1, SearchHit o2) {
            return Integer.valueOf(o1.getCharge()).compareTo(Integer.valueOf(o2.getCharge()));
        }
    }
    
    private void calculateFdr(List<SearchHit> forwardList, List<SearchHit> reverseList, double thresholdFdr) {
        
       log.info("Forward PSM count: "+forwardList.size()+"; Reverse PSM count: "+reverseList.size());
       
        if (forwardList.size() == 0) {
           return;
       }
       
       if (reverseList.size() == 0) {
           for (SearchHit hit: forwardList)
               hit.getPeptideSequenceMatch().setFilterScore(new FilterScore("fdr", 0.0, true));
           return;
       }
       
       Collections.sort(forwardList, Collections.reverseOrder(psmComparator));
       Collections.sort(reverseList, Collections.reverseOrder(psmComparator));
       log.debug("\tMax score for forward list: "+forwardList.get(0).getScore()+"; #HITS: "+forwardList.size());
       log.debug("\tMax score for reverse list: "+reverseList.get(0).getScore()+"; #HITS: "+reverseList.size());
       
       
       int lastReverseIndex = 0;
       int lastForwardIndex = 0;

       SearchHit reverseHit = null;
       // calculate fdr at each score
       for (SearchHit hit: forwardList) {
           lastForwardIndex++;
           // get the number of pms in the reverseMatchList that have a score >= the score of this psm
           for (; lastReverseIndex < reverseList.size(); lastReverseIndex++) {
               reverseHit = reverseList.get(lastReverseIndex);
               if (psmComparator.compare(hit, reverseHit) == 1)
                   break;
           }
           // double fdr = (double)(2 * lastReverseIndex) / (double)(lastReverseIndex + lastForwardIndex);
           double fdr = Math.min(1.0, (double)(lastReverseIndex*(1+decoyRatio)) / (double)(lastReverseIndex + lastForwardIndex));
           boolean accepted = fdr <= thresholdFdr ? true : false;
           hit.getPeptideSequenceMatch().setFilterScore(new FilterScore("fdr", fdr, accepted));
       }
    }
}
