package edu.uwpr.protinfer.fdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;

import edu.uwpr.protinfer.ProteinNode;
import edu.uwpr.protinfer.fdr.PeptideSequenceMatch.PsmComparator;
import edu.uwpr.protinfer.graph.InvalidNodeException;

public class FdrCalculator {

    private List<PeptideSequenceMatch> forwardMatchList;
    private List<PeptideSequenceMatch> reverseMatchList;
    private Comparator<PeptideSequenceMatch> psmComparator;

    public FdrCalculator(Comparator<PeptideSequenceMatch> psmComparator) {
        this.forwardMatchList = new ArrayList<PeptideSequenceMatch>();
        this.reverseMatchList = new ArrayList<PeptideSequenceMatch>();
        this.psmComparator = psmComparator;
    }

    public void addForwardPsm(PeptideSequenceMatch psm) {
        forwardMatchList.add(psm);
    }

    public void addReversePsm(PeptideSequenceMatch psm) {
        reverseMatchList.add(psm);
    }

    public List<PeptideSequenceMatch> calculateFdr(double thresholdFdr) {
        // sort the matches
        Collections.sort(forwardMatchList, Collections.reverseOrder(psmComparator));
        Collections.sort(reverseMatchList, Collections.reverseOrder(psmComparator));
        System.out.println("Max score for forward list: "+forwardMatchList.get(0).getMatchScore());
        System.out.println("Max score for reverse list: "+reverseMatchList.get(0).getMatchScore());

        int lastReverseIndex = 0;
        int lastForwardIndex = 0;

        PeptideSequenceMatch reversePsm = null;
        // calculate fdr at each score
        for (PeptideSequenceMatch psm: forwardMatchList) {
            lastForwardIndex++;
            // get the number of pms in the reverseMatchList that have a score >= the score of this psm
            for (; lastReverseIndex < reverseMatchList.size(); lastReverseIndex++) {
                reversePsm = reverseMatchList.get(lastReverseIndex);
                if (psmComparator.compare(psm, reversePsm) == 1)
                    break;
            }
            double fdr = (double)(2 * lastReverseIndex) / (double)(lastReverseIndex + lastForwardIndex);
            if (fdr > thresholdFdr)
                break;

            psm.setFdr(fdr);
        }
        return forwardMatchList;
    }

    public static void main(String[] args) {

        FdrCalculator fdrCalc = new FdrCalculator(new PsmComparator());

        String file = "TEST_DATA/large/PARC_depleted_b1_02.sqt";
        SequestSQTFileReader reader = new SequestSQTFileReader();
        int peptideCount = 0;
        int forwardCount = 0;
        int reverseCount = 0;
        try {
            reader.open(file, false);
            reader.getSearchHeader();
            SequestSearchScan scan = null;
            while(reader.hasNextSearchScan()) {
                scan = reader.getNextSearchScan();
//              System.out.println("Scan: "+scan.getScanNumber());
                Set<String> accessions = new HashSet<String>();
                for (SequestSearchResultIn result: scan.getScanResults()) {
                    PeptideSequenceMatch psm = new PeptideSequenceMatch();
                    psm.setMatchScore(result.getSequestResultData().getxCorr().doubleValue());
                    
                    List<MsSearchResultProteinIn> proteinList = result.getProteinMatchList();
                    for (MsSearchResultProteinIn prot: proteinList)
                        accessions.add(prot.getAccession());
                    
                    SequestResultData scores = result.getSequestResultData();
                    for (String acc: accessions) {
                        if (acc.startsWith("Reverse")) {
                            fdrCalc.addReversePsm(psm);
                            reverseCount++;
                        }
                        else {
                            fdrCalc.addForwardPsm(psm);
                            forwardCount++;
                        }
                    }
                    peptideCount++;
                }
//              if (peptideCount >  100)
//              break;
            }
            reader.close();
            System.out.println("Finished reading file: #peptide: "+peptideCount+"; forward matches: "+forwardCount+"; reverse matches: "+reverseCount);
            
            List<PeptideSequenceMatch> sortedPsms = fdrCalc.calculateFdr(0.05);
            int cnt = 0;
            for (PeptideSequenceMatch psm: sortedPsms) {
                System.out.println(psm);
                if (psm.getFdr() == 1.0)
                    break;
                cnt++;
            }
            System.out.println("Number of hits above fdr threshold of 0.05: "+cnt);
            
            
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
    }
}
