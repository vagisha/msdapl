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

import edu.uwpr.protinfer.fdr.PeptideSequenceMatch.PsmComparator;
import edu.uwpr.protinfer.pepxml.PepxmlFileReader;
import edu.uwpr.protinfer.pepxml.ScanSearchResult;
import edu.uwpr.protinfer.pepxml.SearchHit;

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

//        String file = "TEST_DATA/large/PARC_depleted_b1_02.sqt";
        String file = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_01.pep.xml";
        parsePepxmlFile(file);
    }
    
    private static void parsePepxmlFile(String file) {
        PepxmlFileReader reader = new PepxmlFileReader();
        FdrCalculator fdrCalc = new FdrCalculator(new PsmComparator());
        int peptideCount = 0;
        int forwardCount = 0;
        int reverseCount = 0;
        try {
            reader.open(file);
            ScanSearchResult scan = null;
            int scanCount = 0;
            int hitCount = 0;
            while(reader.hasNextScanSearchResult()) {
                scanCount++;
                scan = reader.getNextSearchScan();
//              System.out.println("Scan: "+scan.getStartScan()+"; #hits: "+scan.getSearchHits().size());
                for (SearchHit hit: scan.getSearchHits()) {
                    hitCount++;
                    PeptideSequenceMatch psm = new PeptideSequenceMatch();
                    psm.setMatchScore(scan.getStartScan(), scan.getAssumedCharge(), hit.getXcorr().doubleValue());

                    String acc = hit.getMatchProteinAccession();
                    if (acc.startsWith("rev_")) {
                        fdrCalc.addReversePsm(psm);
                        reverseCount++;
                    }
                    else {
                        fdrCalc.addForwardPsm(psm);
                        forwardCount++;
                    }
                    peptideCount++;
                }
//              if (peptideCount >  100)
//              break;
            }
            reader.close();
            System.out.println("Number of scans: "+scanCount+"; hitCount: "+hitCount);
            System.out.println("Finished reading file: #peptide: "+peptideCount+"; forward matches: "+forwardCount+"; reverse matches: "+reverseCount);
            
            List<PeptideSequenceMatch> sortedPsms = fdrCalc.calculateFdr(0.25);
            List<PeptideSequenceMatch> acceptedPsms = new ArrayList<PeptideSequenceMatch>();
            for (PeptideSequenceMatch psm: sortedPsms) {
                double fdr = round(psm.getFdr(), 2);
                if (fdr > 0.25)
                    break;
                if (fdr <= 0.25)
                    acceptedPsms.add(psm);
            }
            System.out.println("Total psm's: "+sortedPsms.size());
            System.out.println("Number of hits above fdr threshold of 0.25: "+acceptedPsms.size());
            int chg1Cnt = 0;
            int chg2Cnt = 0;
            int chg3Cnt = 0;
            for (PeptideSequenceMatch psm: acceptedPsms) {
                if (psm.getMatchCharge() == 1)
                    chg1Cnt++;
                else if (psm.getMatchCharge() == 2)
                    chg2Cnt++;
                else if (psm.getMatchCharge() == 3)
                    chg3Cnt++;
            }
            
            System.out.println("# Accepted at +1: "+chg1Cnt);
            System.out.println("# Accepted at +2: "+chg2Cnt);
            System.out.println("# Accepted at +3: "+chg3Cnt);
            
            // sort the results by scan number and print them
//            Collections.sort(sortedPsms, new Comparator<PeptideSequenceMatch>() {
//                public int compare(PeptideSequenceMatch o1,
//                        PeptideSequenceMatch o2) {
//                    return Integer.valueOf(o1.getScanNumber()).compareTo(Integer.valueOf(o2.getScanNumber()));
//                }});
//            
//            for (PeptideSequenceMatch psm: sortedPsms) {
//                System.out.println(psm);
//            }
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
    }
    
    private static double round(double value, int decimalPlaces) {
        return (Math.round((value*Math.pow(10, decimalPlaces))))/(Math.pow(10, decimalPlaces));
    }
    
    private static void parseSQTFile(String file) {
        
        FdrCalculator fdrCalc = new FdrCalculator(new PsmComparator());
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
                    psm.setMatchScore(result.getScanNumber(), result.getCharge(), result.getSequestResultData().getxCorr().doubleValue());
                    
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
