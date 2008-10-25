package edu.uwpr.protinfer.idpicker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.ProteinHit;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculator;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.filter.fdr.FdrFilterable;
import edu.uwpr.protinfer.pepxml.InteractPepXmlFileReader;
import edu.uwpr.protinfer.pepxml.ScanSearchResult;
import edu.uwpr.protinfer.pepxml.SequestSearchHit;

public class FdrCalculatorIdPicker <T extends FdrCandidateHasCharge> extends FdrCalculator<T> {

    private double decoyRatio = 1.0;
    private boolean separateChargeStates = false;
    

    public void setDecoyRatio(double decoyRatio) throws FdrCalculatorException {
        if (decoyRatio <= 0.0 || decoyRatio > 1.0)
            throw new FdrCalculatorException("Invalid threshold FDR. Decoy ratio should be < 0.0 and >=1.0");
        this.decoyRatio = decoyRatio;
    }
    
    public void separateChargeStates(boolean separate) {
        separateChargeStates = separate;
    }

    @Override
    protected double calculateFdr(int targetCount, int decoyCount) {
//        double fdr = (double)(2 * decoyCount) / (double)(decoyCount + targetCount);
        return Math.min(1.0, (double)(decoyCount*(1+decoyRatio)) / (double)(decoyCount + targetCount));
    }

    @Override
    protected boolean considerCandidate(T candidate) {
        return !(candidate.isDecoy() && candidate.isTarget());
    }
    
    public void calculateFdr(List<T> candidates, Comparator<T> comparator) {
        if (!separateChargeStates)
            super.calculateFdr(candidates, comparator);
        else {
            
            // sort by charge
            Collections.sort(candidates, new Comparator<FdrCandidateHasCharge>() {
                @Override
                public int compare(FdrCandidateHasCharge o1, FdrCandidateHasCharge o2) {
                    return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
                }});
            
            List<T> candidatesWithCharge = new ArrayList<T>();
            int currentChg = 1;
            for (T candidate: candidates) {
                if (candidate.getCharge() != currentChg) {
                    if (candidatesWithCharge.size() > 0)
                        super.calculateFdr(candidatesWithCharge, comparator);
                    
                    candidatesWithCharge.clear();
                    currentChg = candidate.getCharge();
                }
                candidatesWithCharge.add(candidate);
            }
            if (candidatesWithCharge.size() > 0)
                super.calculateFdr(candidatesWithCharge, comparator);
            
        }
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
                
                
                int peptideCount = 0;
                int forwardCount = 0;
                int reverseCount = 0;
                List<SpectrumHit> hits = new ArrayList<SpectrumHit>();
                
                while(reader.hasNextScanSearchResult()) {
                    scanCount++;
                    scan = reader.getNextSearchScan();
//                  System.out.println("Scan: "+scan.getStartScan()+"; #hits: "+scan.getSearchHits().size());
                    for (SequestSearchHit hit: scan.getSearchHits()) {
                        hitCount++;
                        peptideCount++;
                    }
                    
                    boolean isTarget = false;
                    boolean isDecoy = false;
                    for (ProteinHit protHit: scan.getTopHit().getProteinHits()) {
                        if (protHit.getAccession().startsWith("rev_"))
                            isDecoy = true;
                        else
                            isTarget = true;
                    }
                    SpectrumHit hit = new SpectrumHit(scan.getStartScan(), scan.getAssumedCharge(), 
                            scan.getTopHit().getXcorr().doubleValue(),
                            isTarget, isDecoy);
                    hits.add(hit);
                    
                    String acc = scan.getTopHit().getFirstProteinHit().getAccession();
                    if (acc.startsWith("rev_")) {
                        reverseCount++;
                    }
                    else {
                        forwardCount++;
                    }
                }
                
                System.out.println("Number of scans: "+scanCount+"; hitCount: "+hitCount);
                System.out.println("Target matches: "+forwardCount+"; Decoy matches: "+reverseCount);
                
                FdrCalculatorIdPicker<SpectrumHit> fdrCalc = new FdrCalculatorIdPicker<SpectrumHit>();
                fdrCalc.separateChargeStates(true);
//                fdrCalc.setDecoyRatio(0.00043935);
                fdrCalc.setDecoyRatio(1.0);
                fdrCalc.calculateFdr(hits, new Comparator<SpectrumHit>() {
                    @Override
                    public int compare(SpectrumHit o1, SpectrumHit o2) {
                        return Double.valueOf(o1.getScore()).compareTo(o2.getScore());
                    }});
                
                
                List<SpectrumHit> targetHits = new ArrayList<SpectrumHit>();
                for(SpectrumHit hit: hits) {
                    if (hit.isTarget())
                        targetHits.add(hit);
                }
                    
                FdrFilterCriteria filterCriteria = new FdrFilterCriteria(0.25);
                List<SpectrumHit> acceptedHits = Filter.filter(targetHits, filterCriteria);
                
                System.out.println("# Accepted hits: "+acceptedHits.size()+" out of "+hits.size()+" total hits");
                Collections.sort(acceptedHits, new Comparator<SpectrumHit>() {

                    public int compare(SpectrumHit o1, SpectrumHit o2) {
                       return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
                    }});
                int lastCharge = 1;
                int acceptedCount = 0;
                for (SpectrumHit hit: acceptedHits) {
                    if (hit.getCharge() != lastCharge) {
                        System.out.println("# hits accepted for charge +"+lastCharge+" "+acceptedCount);
                        lastCharge = hit.getCharge();
                        acceptedCount = 0;
                    }
                    acceptedCount++;
                }
                System.out.println("# hits accepted for charge +"+lastCharge+" "+acceptedCount);
//                printAcceptedPsms(dir+File.separator+new File(runName).getName(), acceptedHits);
                
                break;
            }
            reader.close();
        }
        catch (DataProviderException e) {
            e.printStackTrace();
        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
        catch (FdrCalculatorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (FilterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                reader.close();
        }
    }
    
    private static void printAcceptedPsms(String runName, List<SpectrumHit> acceptedHits) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(runName+".psm"));
        for (SpectrumHit hit: acceptedHits) {
            writer.write(hit.getScan()+"\t"+hit.getCharge()+"\t"+hit.getScore()+"\t"+hit.getFdr()+"\n");
//            PeptideHit hit = hit.getScanSearchResult().getTopHit().getPeptide();
//            writer.write("\t"+hit.getPeptideSeq());
//            StringBuilder buf = new StringBuilder();
//            for (ProteinHit p: hit.getProteinList()) {
//                buf.append(","+p.getAccession());
//            }
//            buf.deleteCharAt(0);
//            writer.write("\t"+buf.toString()+"\n");
        }
        writer.close();
    }

    private static double round(double value, int decimalPlaces) {
        return (Math.round((value*Math.pow(10, decimalPlaces))))/(Math.pow(10, decimalPlaces));
    }
    
    private static final class SpectrumHit implements FdrCandidateHasCharge, FdrFilterable {

        private int scan;
        private int charge;
        private double xcorr;
        private double fdr; 
        private boolean isDecoy;
        private boolean isTarget;
        private boolean isAccepted = false;
        
        public SpectrumHit(int scan, int charge, double xcorr, boolean isTarget, boolean isDecoy) {
            this.scan = scan;
            this.charge = charge;
            this.xcorr = xcorr;
            this.isDecoy = isDecoy;
            this.isTarget = isTarget;
        }
        
        @Override
        public int getCharge() {
            return charge;
        }

        public int getScan() {
            return scan;
        }
        
        public double getScore() {
            return xcorr;
        }
        
        @Override
        public double getFdr() {
            return fdr;
        }

        @Override
        public boolean isDecoy() {
            return isDecoy;
        }

        @Override
        public boolean isTarget() {
            return isTarget;
        }

        @Override
        public void setFdr(double fdr) {
            this.fdr = fdr;
        }

        @Override
        public boolean isAccepted() {
            return isAccepted;
        }

        @Override
        public void setAccepted(boolean accepted) {
            this.isAccepted = accepted;
        }
    }
}
