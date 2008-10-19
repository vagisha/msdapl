package edu.uwpr.protinfer.pepxml;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.parser.DataProviderException;

public class PepxmlFileReader {

    private String filePath;
    private XMLStreamReader reader = null;
    
    public void open(String filePath) throws DataProviderException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            InputStream input = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(input);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        
        
        this.filePath = filePath;
    }
    
    public void close() {
        if (reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
    }
    
    public boolean hasNextScanSearchResult() throws DataProviderException  {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                        return true;
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    public ScanSearchResult getNextSearchScan() throws DataProviderException {
        ScanSearchResult scanResult = new ScanSearchResult();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("spectrum"))
                scanResult.setSpectrumString(val);
            else if (attrib.equalsIgnoreCase("start_scan"))
                scanResult.setStartScan(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("end_scan"))
                scanResult.setEndScan(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("precursor_neutral_mass"))
                scanResult.setPrecursorNeutralMass(new BigDecimal(val));
            else if (attrib.equalsIgnoreCase("assumed_charge"))
                scanResult.setAssumedCharge(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("retention_time_sec"))
                scanResult.setRetentionTime(Float.parseFloat(val));
        }
        // read the search hits for this scan
        SearchHit hit = null;
        try {
            while(reader.hasNext()) {
                int evtType = reader.next();
                if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                    break;
                if (evtType != XMLStreamReader.START_ELEMENT)
                    continue;
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase("search_hit")) {
                        if (hit != null)
                            scanResult.addSearchHit(hit);
                        hit = new SearchHit();
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            String attrib = reader.getAttributeLocalName(i);
                            String val = reader.getAttributeValue(i);
                            if (attrib.equalsIgnoreCase("hit_rank"))
                                hit.setXcorr(new BigDecimal(val));
                            else if (attrib.equalsIgnoreCase("peptide"))
                                hit.setPeptide(val);
                            else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                                hit.setPreResidue(Character.valueOf(val.charAt(0)));
                            else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                                hit.setPostResidue(Character.valueOf(val.charAt(0)));
                            else if (attrib.equalsIgnoreCase("protein"))
                                hit.addProteinHit(new ProteinHit(val));
                            else if (attrib.equalsIgnoreCase("num_tot_proteins"))
                                hit.setNumMatchingProteins(Integer.parseInt(val));
                            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                                hit.setNumMatchedIons(Integer.parseInt(val));
                            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                                hit.setNumPredictedIons(Integer.parseInt(val));
                            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass"))
                                hit.setCalcNeutralMass(new BigDecimal(val));
                        }
                    }

                    else if (reader.getLocalName().equalsIgnoreCase("alternative_protein")) {
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            String attrib = reader.getAttributeLocalName(i);
                            String val = reader.getAttributeValue(i);
                            if (attrib.equalsIgnoreCase("protein"))
                                hit.addProteinHit(new ProteinHit(val));
                        }
                    }
                    
                    else if (reader.getLocalName().equalsIgnoreCase("search_score")) {
                        String scoreType = reader.getAttributeValue(null, "name");
                        String scoreVal = reader.getAttributeValue(null, "value");
                        if (scoreType.equalsIgnoreCase("xcorr"))
                            hit.setXcorr(new BigDecimal(scoreVal));
                        else if (scoreType.equalsIgnoreCase("deltacn"))
                            hit.setDeltaCn(new BigDecimal(scoreVal));
                        else if (scoreType.equalsIgnoreCase("spscore"))
                            hit.setSpScore(new BigDecimal(scoreVal));
                        else if (scoreType.equalsIgnoreCase("sprank"))
                            hit.setSpRank(Integer.parseInt(scoreVal));
                    }
                }
            }
            // add the last one
            if (hit != null)
                scanResult.addSearchHit(hit);
        }
        catch (NumberFormatException e) {
            throw new DataProviderException("Error parsing number in file: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return scanResult;
    }
    
    public static void main(String[] args) throws DataProviderException, IOException {
        String filePath = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_04.pep.xml";
        PepxmlFileReader reader = new PepxmlFileReader();
        reader.open(filePath);
        int scanCount = 0;
        int targetHitcount = 0;
        int decoyHitCount = 0;
        int ambiHitCount = 0;
        
        while(reader.hasNextScanSearchResult()) {
            ScanSearchResult scanResult = reader.getNextSearchScan();
            if (scanResult.getSearchHits().size() != 1)
                System.out.println("Scan has "+scanResult.getSearchHits().size()+" hits!!!");
            for (SearchHit hit: scanResult.getSearchHits()) {
                List<ProteinHit> proteins = hit.getProteinHits();
//                if (proteins.size() != 1)
//                    System.out.println("Hit "+scanResult.getSpectrumString()+" has "+proteins.size()+" matching proteins");
                boolean target = false;
                boolean decoy = false;
                for (ProteinHit prot: proteins) {
                    if (prot.getAccession().startsWith("rev_"))
                        decoy = true;
                    else
                        target = true;
                }
                if (target && !decoy)   targetHitcount++;
                if (decoy && !target)   decoyHitCount++;
                if (decoy && target)    ambiHitCount++;
            }
            
//            System.out.println(scanResult.getSpectrumString()+": #hits: "+scanResult.getSearchHits().size());
            scanCount++;
        }
        reader.close();
        
        System.out.println("Number of spectrum_query elements: "+scanCount);
        System.out.println("Target Hits: "+targetHitcount+"; Decoy Hits: "+decoyHitCount+"; Ambig. Hits: "+ambiHitCount);
    }
    
//    public static void main(String[] args) throws DataProviderException, IOException {
//        String filePath = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_01.pep.xml";
//        String outFileF = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_01.fwd";
//        String outFileR = "TEST_DATA/for_vagisha/18mix/JE102306_102306_18Mix4_Tube1_01.rev";
//        PepxmlFileReader reader = new PepxmlFileReader();
//        reader.open(filePath);
//        int scanCount = 0;
//        BufferedWriter writerF = new BufferedWriter(new FileWriter(outFileF));
//        BufferedWriter writerR = new BufferedWriter(new FileWriter(outFileR));
//        while(reader.hasNextScanSearchResult()) {
//            ScanSearchResult scanResult = reader.getNextSearchScan();
//            for (SearchHit hit: scanResult.getSearchHits()) {
//                if (hit.getProteinHits().get(0).getAccession().startsWith("rev_")) {
//                    writerR.write(scanResult.getStartScan()+"\t"+scanResult.getAssumedCharge()+"\t"+hit.getXcorr().doubleValue()+"\n");
//                }
//                else
//                    writerF.write(scanResult.getStartScan()+"\t"+scanResult.getAssumedCharge()+"\t"+hit.getXcorr().doubleValue()+"\n");
//            }
//            System.out.println(scanResult.getSpectrumString()+": #hits: "+scanResult.getSearchHits().size());
//            scanCount++;
//        }
//        reader.close();
//        if (writerF != null)
//            writerF.close();
//        if (writerR != null)
//            writerR.close();
//        
//        System.out.println("Number of spectrum_query elements: "+scanCount);
//    }
}

