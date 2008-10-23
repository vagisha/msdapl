package edu.uwpr.protinfer.filter.idpicker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.PeptideHit;
import edu.uwpr.protinfer.PeptideSequenceMatch;
import edu.uwpr.protinfer.Protein;
import edu.uwpr.protinfer.ProteinHit;
import edu.uwpr.protinfer.PeptideSequenceMatch.PsmComparator;
import edu.uwpr.protinfer.pepxml.InteractPepXmlFileReader;
import edu.uwpr.protinfer.pepxml.ScanSearchResult;
import edu.uwpr.protinfer.pepxml.SequestSearchHit;

public class FdrCalculator {

    private List<PeptideSequenceMatch> forwardMatchList;
    private List<PeptideSequenceMatch> reverseMatchList;
    private Comparator<PeptideSequenceMatch> psmComparator;
    private double decoyRatio = 1.0;

    public FdrCalculator(Comparator<PeptideSequenceMatch> psmComparator) {
        this.forwardMatchList = new ArrayList<PeptideSequenceMatch>();
        this.reverseMatchList = new ArrayList<PeptideSequenceMatch>();
        this.psmComparator = psmComparator;
    }

    public void setDecoyRatio(double decoyRatio) {
        this.decoyRatio = decoyRatio;
    }
    
    public void addForwardPsm(PeptideSequenceMatch psm) {
        forwardMatchList.add(psm);
    }

    public void addReversePsm(PeptideSequenceMatch psm) {
        reverseMatchList.add(psm);
    }

    public List<PeptideSequenceMatch> calculateFdr(double thresholdFdr, boolean separateChargeStates) {
        
        System.out.println("Calculating FDR for "+forwardMatchList.size()+" PSMs");
        
        if (!separateChargeStates) {
            List<PeptideSequenceMatch> acceptedPsms = calculateFdr(forwardMatchList, reverseMatchList, thresholdFdr);
            System.out.println("# Accepted PSMs (target) at fdr "+thresholdFdr+" -- "+acceptedPsms.size());
            return acceptedPsms;
        }
        
        else {
            List<PeptideSequenceMatch> acceptedPsms = new ArrayList<PeptideSequenceMatch>();
            Collections.sort(forwardMatchList, new PsmChargeComparator()); // sort by charge
            Collections.sort(reverseMatchList, new PsmChargeComparator()); // sort by charge
            int currentChg = 1;
            List<PeptideSequenceMatch> forwardList = new ArrayList<PeptideSequenceMatch>();
            List<PeptideSequenceMatch> reverseList = new ArrayList<PeptideSequenceMatch>();
            int fidx = 0;
            int ridx = 0;
            while(true) {
                int nextChg = currentChg;
                for (; fidx < forwardMatchList.size(); fidx++) {
                    PeptideSequenceMatch psm = forwardMatchList.get(fidx);
                    if (psm.getCharge() != currentChg) {
                        nextChg = psm.getCharge();
                        break;
                    }
                    else {
                        forwardList.add(psm);
                    }
                }
                for (; ridx < reverseMatchList.size(); ridx++) {
                    PeptideSequenceMatch psm = reverseMatchList.get(ridx);
                    if (psm.getCharge() != currentChg) {
                        break;
                    }
                    else
                        reverseList.add(psm);
                }
                System.out.println("Calculating FDR for +"+currentChg+" PSMs");
                List<PeptideSequenceMatch> acceptedChgPsms = calculateFdr(forwardList, reverseList, thresholdFdr);
                System.out.println("# Accepted PSMs (target) at charge +"+currentChg+" and fdr "+thresholdFdr+" -- "+acceptedChgPsms.size());
                if (acceptedChgPsms != null && acceptedChgPsms.size() > 0)
                    acceptedPsms.addAll(acceptedChgPsms);
                
                // have we seen all the forward sequence matches? 
                if (fidx >= forwardMatchList.size())
                    break;
                
                currentChg = nextChg;
                forwardList.clear();
                reverseList.clear();
            }
            System.out.println("# Accepted PSMs (target) at fdr "+thresholdFdr+" -- "+acceptedPsms.size());
            return acceptedPsms;
        }
    }

    private static final class PsmChargeComparator implements Comparator<PeptideSequenceMatch> {

        public int compare(PeptideSequenceMatch o1, PeptideSequenceMatch o2) {
            return Integer.valueOf(o1.getCharge()).compareTo(Integer.valueOf(o2.getCharge()));
        }
    }
    
    private List<PeptideSequenceMatch> calculateFdr(List<PeptideSequenceMatch> forwardList, List<PeptideSequenceMatch> reverseList, double thresholdFdr) {
       System.out.println("Forward PSM count: "+forwardList.size()+"; Reverse PSM count: "+reverseList.size());
       
        if (forwardList.size() == 0) {
           return new ArrayList<PeptideSequenceMatch>(0);
       }
       
       if (reverseList.size() == 0) {
           for (PeptideSequenceMatch psm: forwardList)
               psm.setFdr(0.0);
           return forwardList;
       }
       
       Collections.sort(forwardList, Collections.reverseOrder(psmComparator));
       Collections.sort(reverseList, Collections.reverseOrder(psmComparator));
       System.out.println("\tMax score for forward list: "+forwardList.get(0).getScore()+"; #HITS: "+forwardList.size());
       System.out.println("\tMax score for reverse list: "+reverseList.get(0).getScore()+"; #HITS: "+reverseList.size());
       
       
       int lastReverseIndex = 0;
       int lastForwardIndex = 0;

       PeptideSequenceMatch reversePsm = null;
       // calculate fdr at each score
       
       for (PeptideSequenceMatch psm: forwardList) {
           lastForwardIndex++;
           // get the number of pms in the reverseMatchList that have a score >= the score of this psm
           for (; lastReverseIndex < reverseList.size(); lastReverseIndex++) {
               reversePsm = reverseList.get(lastReverseIndex);
               if (psmComparator.compare(psm, reversePsm) == 1)
                   break;
           }
//           double fdr = (double)(2 * lastReverseIndex) / (double)(lastReverseIndex + lastForwardIndex);
           double fdr = Math.min(1.0, (double)(lastReverseIndex*(1+decoyRatio)) / (double)(lastReverseIndex + lastForwardIndex));
           psm.setFdr(fdr);
       }
       List<PeptideSequenceMatch> acceptedPsms = new ArrayList<PeptideSequenceMatch>();
       for (PeptideSequenceMatch psm: forwardList) {
           if (psm.getFdr() <= thresholdFdr)
               acceptedPsms.add(psm);
       }
       return acceptedPsms;
    }
    
    
    public static void main(String[] args) {

//        String file = "TEST_DATA/large/PARC_depleted_b1_02.sqt";
//        String file = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_01.pep.xml";
        String dir = "TEST_DATA/for_vagisha/18mix";
        String file = "interact.pep.xml";
        parseInteractPepxmlFile(dir, file);
    }
    
    private static void parseInteractPepxmlFile(String dir, String fileName) {
        InteractPepXmlFileReader reader = new InteractPepXmlFileReader();
        
        try {
            reader.open(dir+File.separator+fileName);
            ScanSearchResult scan = null;
            int scanCount = 0;
            int hitCount = 0;
            while(reader.hasNextRunSummary()) {
                String runName = reader.getRunName();
                System.out.println("Summary for file: "+runName);
                FdrCalculator fdrCalc = new FdrCalculator(new PsmComparator());
                int peptideCount = 0;
                int forwardCount = 0;
                int reverseCount = 0;
                while(reader.hasNextScanSearchResult()) {
                    scanCount++;
                    scan = reader.getNextSearchScan();
//                  System.out.println("Scan: "+scan.getStartScan()+"; #hits: "+scan.getSearchHits().size());
                    for (SequestSearchHit hit: scan.getSearchHits()) {
                        hitCount++;
                        peptideCount++;
                    }
                    PeptideSequenceMatch psm = new PeptideSequenceMatch(scan, scan.getStartScan(), scan.getAssumedCharge());
                    String acc = scan.getTopHit().getFirstProteinHit().getAccession();
                    if (acc.startsWith("rev_")) {
                        fdrCalc.addReversePsm(psm);
                        reverseCount++;
                    }
                    else {
                        fdrCalc.addForwardPsm(psm);
                        forwardCount++;
                    }

                }
                System.out.println("Number of scans: "+scanCount+"; hitCount: "+hitCount);
                System.out.println("Finished reading file: forward matches: "+forwardCount+"; reverse matches: "+reverseCount);
//                fdrCalc.setDecoyRatio(0.00043935);
                fdrCalc.setDecoyRatio(1.0);
                List<PeptideSequenceMatch> acceptedPsms = fdrCalc.calculateFdr(0.25, true);
                Map<String, PeptideHit> peptideHits = new HashMap<String, PeptideHit>();
                Map<String, Protein> proteinHits = new HashMap<String, Protein>();
                
                for (PeptideSequenceMatch psm: acceptedPsms) {
                    SequestSearchHit hit = psm.getScanSearchResult().getTopHit();
                    PeptideHit pHit = hit.getPeptide();
                    peptideHits.put(pHit.getPeptideSeq(), pHit);
                    for (ProteinHit prHit: pHit.getProteinList()) {
                        proteinHits.put(prHit.getAccession(), prHit.getProtein());
                    }
                }
                System.out.println("Number of peptides found: "+peptideHits.size());
                System.out.println("Number of proteins found: "+proteinHits.size());
                System.out.println("\n\n");
                
                printAcceptedPsms(dir+File.separator+new File(runName).getName(), acceptedPsms);
            }
            reader.close();
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
    }
    
    private static void printAcceptedPsms(String runName,
            List<PeptideSequenceMatch> acceptedPsms) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(runName+".psm"));
        for (PeptideSequenceMatch psm: acceptedPsms) {
            writer.write(psm.getScanNumber()+"\t"+psm.getCharge()+"\t"+psm.getScore()+"\t"+psm.getFdr());
            PeptideHit hit = psm.getScanSearchResult().getTopHit().getPeptide();
            writer.write("\t"+hit.getPeptideSeq());
            StringBuilder buf = new StringBuilder();
            for (ProteinHit p: hit.getProteinList()) {
                buf.append(","+p.getAccession());
            }
            buf.deleteCharAt(0);
            writer.write("\t"+buf.toString()+"\n");
        }
        writer.close();
    }

    private static double round(double value, int decimalPlaces) {
        return (Math.round((value*Math.pow(10, decimalPlaces))))/(Math.pow(10, decimalPlaces));
    }
}
