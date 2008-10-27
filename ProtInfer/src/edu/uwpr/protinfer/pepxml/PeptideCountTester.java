package edu.uwpr.protinfer.pepxml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.parser.DataProviderException;

import edu.uwpr.protinfer.infer.ProteinHit;

public class PeptideCountTester {

    public static void main(String[] args) throws DataProviderException, IOException {
        
        String dir = "TEST_DATA/for_vagisha/human";
        String fastaDb = "ipi.HUMAN.fasta.20080402.for_rev";
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
                for (SequestSearchHit hit: scanResult.getSearchHits()) {
                    
                    List<ProteinHit> proteins = hit.getProteinHits();
                    boolean target = false;
                    boolean decoy = false;
                    for (ProteinHit prot: proteins) {
                        if (prot.getAccession().startsWith("rev_")) {
                            decoy = true;
                            // have we seen this protein before? 
                            ProteinInfo pinfo = revProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            pinfo.spectrumCount++;
                            pinfo.peptides.add(hit.getPeptide().getPeptideSequence());
                            revProtCoverage.put(prot.getAccession(), pinfo);
                        }
                        else {
                            
                            target = true;
                            // have we seen this protein before? 
                            ProteinInfo pinfo = fwdProtCoverage.get(prot.getAccession());
                            if (pinfo == null) {
                                pinfo = new ProteinInfo();
                            }
                            pinfo.spectrumCount++;
                            pinfo.peptides.add(hit.getPeptide().getPeptideSequence());
                            fwdProtCoverage.put(prot.getAccession(), pinfo);
                        }
                    }
                    peptidesFound.add(hit.getPeptide().getPeptideSequence());
                    
                    if (target && !decoy)   targetHitcount++;
                    if (decoy && !target)   decoyHitCount++;
                    if (decoy && target)    ambiHitCount++;
                }
                scanCount++;
            }
            
            calculateProteinCoverage(fastaDb, fwdProtCoverage);
            calculateProteinCoverage(fastaDb, revProtCoverage);
            
            System.out.println("\tNumber of spectrum_query elements: "+scanCount);
            System.out.println("\tTarget Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
            System.out.println("\tNumber of peptides found: "+reader.getPeptideHits().size());
            System.out.println("\tNumber of proteins found: "+reader.getProteinHits().size());
            
            System.out.println("Number of proteins identified FWD: "+fwdProtCoverage.size());
            System.out.println("Number of proteins identified REV: "+revProtCoverage.size());
            
            printStats(dir+File.separator+runName+"_F_.stats.txt", fwdProtCoverage);
            printStats(dir+File.separator+runName+"_R_.stats.txt", revProtCoverage);
        }
        
        reader.close();
    }

    private static void calculateProteinCoverage(String fastaFile, Map<String, ProteinInfo> protCoverage) {
        
        Pattern pattern = Pattern.compile(">(\\S+)\\s+.*");
        
        // read in the fasta file
        Map<String, String> proteins = readFastaFile(fastaFile, pattern);
        System.out.println("Number of proteins in fasta file: "+proteins.size());
        
        for (String accession: protCoverage.keySet()) {
            
            Set<String> peptides = protCoverage.get(accession).peptides;
            
            List<Coordinates> coords = new ArrayList<Coordinates>(peptides.size());
            for (String peptide: peptides) {
                int nextStart = 0;
                int idx;
                while((idx = accession.indexOf(peptide, nextStart)) >= 0) {
                    nextStart = idx+peptide.length();
                    coords.add(new Coordinates(idx, nextStart - 1));
                }
            }
            
            Collections.sort(coords, new Comparator<Coordinates>(){
                public int compare(Coordinates o1, Coordinates o2) {
                    return Integer.valueOf(o1.start).compareTo(Integer.valueOf(o2.start));
                }});
            
            int coveredLength = 0;
            Coordinates lastCoord = null;
            for (Coordinates coord: coords) {
                if (lastCoord == null) {
                    lastCoord = coord;
                    continue;
                }
                if (lastCoord.overlap(coord)) {
                    lastCoord.end = Math.max(lastCoord.end, coord.end);
                }
                else {
                    coveredLength += lastCoord.length();
                    lastCoord = coord;
                }
            }
            
            String protein = proteins.get(accession);
            ProteinInfo pinfo = protCoverage.get(accession);
            pinfo.sequenceLen = protein.length();
            pinfo.lenCovered = coveredLength;
        }
    }
    
    

    private static final class Coordinates {
        private int start;
        private int end;
        public Coordinates(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        public boolean overlap(Coordinates coord) {
            if (this.start < coord.start) {
                return coord.start <= this.end;
            }
            else {
                return this.start <= coord.end;
            }
        }
        
        public int length() {
            return end - start + 1;
        }
    }
    
    private static Map<String, String> readFastaFile(String fastaFile,
            Pattern pattern) {
        Map<String, String> proteins = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
            String line = reader.readLine();
            StringBuilder protein = new StringBuilder();
            String accession = null;
            while(line != null) {
                if (line.startsWith(">")) {
                    if (accession != null) {
                        proteins.put(accession, protein.toString());
                        protein = new StringBuilder();
                    }
                    else {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.matches()) {
                            accession = matcher.group(1);
                        }
                        if (accession == null) {
                            System.out.println("Accession cannot be null");
                            System.exit(1);
                        }
                    }
                }
                else if (line.trim().length() != 0) {
                    protein.append(line.trim());
                }
                line = reader.readLine();
            }
            // put the last one in
            proteins.put(accession, protein.toString());
            reader.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return proteins;
    }
    
    private static void printStats(String file, Map<String, ProteinInfo> protCoverage) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Accession\tHitCount\tPeptideCount\tCoverage\n");
            for (String key: protCoverage.keySet()) {
                ProteinInfo pinfo = protCoverage.get(key);
                writer.write(key+"\t"+pinfo.peptides.size()+"\t"+pinfo.spectrumCount+"\t"+pinfo.sequenceLen+"\t"+pinfo.lenCovered+"\n");
            }
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ProteinInfo {
        int spectrumCount = 0;
        int sequenceLen;
        int lenCovered;
        Set<String> peptides = new HashSet<String>();
    }
}

