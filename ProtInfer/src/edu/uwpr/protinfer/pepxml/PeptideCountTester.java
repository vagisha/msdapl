package edu.uwpr.protinfer.pepxml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.ProteinHit;

public class PeptideCountTester {

    public static void main(String[] args) throws DataProviderException, IOException {
        
        String dir = "TEST_DATA/for_vagisha/human";
        String filePath = dir+File.separator+"interact.pep.xml";
        
        InteractPepXmlFileReader reader = new InteractPepXmlFileReader();
        reader.open(filePath);


        Set<String> peptidesFound = new HashSet<String>();
        
        while(reader.hasNextRunSummary()) {
            String runName = new File(reader.getRunName()).getName();
            System.out.println("Results for run: "+reader.getRunName());
            int scanCount = 0;
            int targetHitcount = 0;
            int decoyHitCount = 0;
            int ambiHitCount = 0;
            
            Map<String, ProteinInfo> fwdProtCoverage = new HashMap<String, ProteinInfo>();
            Map<String, ProteinInfo> revProtCoverage = new HashMap<String, ProteinInfo>();
            
            
            while(reader.hasNextScanSearchResult()) {
                ScanSearchResult scanResult = reader.getNextSearchScan();
                if (scanResult.getSearchHits().size() != 1)
                    System.out.println("Scan has "+scanResult.getSearchHits().size()+" hits!!!");
                for (SearchHit hit: scanResult.getSearchHits()) {
                    
                    // if we have already seen this peptide go on to the next one
//                    if (peptidesFound.contains(hit.getPeptide().getPeptideSeq()))
//                        continue;
                    
//                    peptidesFound.add(hit.getPeptide().getPeptideSeq());
                    
                    List<ProteinHit> proteins = hit.getProteinHits();
                    boolean target = false;
                    boolean decoy = false;
                    for (ProteinHit prot: proteins) {
                        if (prot.getAccession().startsWith("rev_")) {
                            decoy = true;
//                            Integer count = revProtCoverage.get(prot.getAccession());
                            ProteinInfo pinfo = revProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            else {
                                pinfo.spectrumCount++;
                                if (!peptidesFound.contains(hit.getPeptide().getPeptideSeq()))
                                    pinfo.peptideCount++;
                            }
                            revProtCoverage.put(prot.getAccession(), pinfo);
                                
                        }
                        else {
                            
                            target = true;
                            ProteinInfo pinfo = fwdProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            else {
                                pinfo.spectrumCount++;
                                if (!peptidesFound.contains(hit.getPeptide().getPeptideSeq()))
                                    pinfo.peptideCount++;
                            }
//                            Integer count = fwdProtCoverage.get(prot.getAccession());
//                            if (count != null)
//                                count++;
//                            else
//                                count = 1;
                            fwdProtCoverage.put(prot.getAccession(), pinfo);
                        }
                    }
                    peptidesFound.add(hit.getPeptide().getPeptideSeq());
                    
                    if (target && !decoy)   targetHitcount++;
                    if (decoy && !target)   decoyHitCount++;
                    if (decoy && target)    ambiHitCount++;
                    
                }

//              System.out.println(scanResult.getSpectrumString()+": #hits: "+scanResult.getSearchHits().size());
                scanCount++;
            }
            System.out.println("\tNumber of spectrum_query elements: "+scanCount);
            System.out.println("\tTarget Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
            System.out.println("\tNumber of peptides found: "+reader.getPeptideHits().size());
            System.out.println("\tNumber of proteins found: "+reader.getProteinHits().size());
            
            System.out.println("Number of proteins identified FWD: "+fwdProtCoverage.size());
            System.out.println("Number of proteins identified REV: "+revProtCoverage.size());
            
//            int totalFwdPeptides = 0;
//            int minFwdPeptides = Integer.MAX_VALUE;
//            int maxFwdPeptides = 0;
//            String maxFwdProt = null;
//            int totalRevPeptides = 0;
//            int minRevPeptides = Integer.MAX_VALUE;
//            int maxRevPeptides = 0;
//            String maxRevProt = null;
//            for (String prot: fwdProtCoverage.keySet()) {
//                Integer count = fwdProtCoverage.get(prot);
//                totalFwdPeptides+=count;
//                if (count > maxFwdPeptides)
//                    maxFwdProt = prot;
//                minFwdPeptides = Math.min(minFwdPeptides, count);
//                maxFwdPeptides = Math.max(maxFwdPeptides, count);
//               
//            }
//            for (String prot: revProtCoverage.keySet()) {
//                Integer count = revProtCoverage.get(prot);
//                totalRevPeptides+=count;
//                if (count > maxRevPeptides)
//                    maxRevProt = prot;
//                minRevPeptides = Math.min(minRevPeptides, count);
//                maxRevPeptides = Math.max(maxRevPeptides, count);
//            }
//            
////            look at: 
////                1. Average # of spectra per peptide for a protein
////                2. Sequence coverage
//            
//            System.out.println("Min Number of peptides / protein FWD: "+minFwdPeptides);
//            System.out.println("Max Number of peptides / protein FWD: "+maxFwdPeptides+"; For protein: "+maxFwdProt);
//            System.out.println("Avg Number of peptides / protein FWD: "+totalFwdPeptides/fwdProtCoverage.size());
//            
//            System.out.println("Min Number of peptides / protein REV: "+minRevPeptides);
//            System.out.println("Max Number of peptides / protein REV: "+maxRevPeptides+"; For protein: "+maxRevProt);
//            System.out.println("Avg Number of peptides / protein REV: "+totalRevPeptides/revProtCoverage.size());
            
            printStats(dir+File.separator+runName+"_F_.stats.txt", fwdProtCoverage);
            printStats(dir+File.separator+runName+"_R_.stats.txt", revProtCoverage);
            
//            break;
        }
        
        reader.close();
    }

    private static void printStats(String file, Map<String, ProteinInfo> protCoverage) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//            Map<Integer, Integer> stats = new HashMap<Integer, Integer>();
            for (String key: protCoverage.keySet()) {
                ProteinInfo pinfo = protCoverage.get(key);
                writer.write(key+"\t"+pinfo.peptideCount+"\t"+pinfo.spectrumCount+"\n");
//                Integer numProtsWithCount = stats.get(peptideCnt);
//                if (numProtsWithCount == null)
//                    numProtsWithCount = 1;
//                else
//                    numProtsWithCount = peptideCnt + 1;
//                stats.put(peptideCnt, numProtsWithCount);
            }
            
//            List<Integer> sortedByPeptideCount = new ArrayList<Integer>(stats.size());
//            sortedByPeptideCount.addAll(stats.keySet());
//            Collections.sort(sortedByPeptideCount);
//            for (Integer peptCnt: sortedByPeptideCount) {
//                writer.write(peptCnt+"\t"+stats.get(peptCnt)+"\n");
//            }
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static class ProteinInfo {
        int peptideCount = 1;
        int spectrumCount = 1;
        int sequenceLen;
        int lenCovered;
    }
}

