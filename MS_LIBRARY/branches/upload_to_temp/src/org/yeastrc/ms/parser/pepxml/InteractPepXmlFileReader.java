/**
 * InteractPepXmlFileReader.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROCPoint;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetResultBean;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.InteractPepXmlDataProvider;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResult;
import org.yeastrc.ms.util.AminoAcidUtils;


/**
 * 
 */
public class InteractPepXmlFileReader implements InteractPepXmlDataProvider<PepXmlSearchScanIn> {

    private static final String MSMS_RUN_SUMMARY = "msms_run_summary";
    private String filePath;
    private XMLStreamReader reader = null;
    private String currentRunSearchName = null;
    private List<MsResidueModificationIn> searchDynamicResidueMods;
    private boolean refreshParserRun = false;
    
    private String peptideProphetVersion;
    private PeptideProphetROC peptideProphetRoc;
    
    public void open(String filePath) throws DataProviderException {
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            InputStream input = new FileInputStream(filePath);
            reader = inputFactory.createXMLStreamReader(input);
            readAnalysisSummary(reader);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        this.filePath = filePath;
    }
    
    private void readAnalysisSummary(XMLStreamReader reader2) throws XMLStreamException {
        
        boolean inPPAnalysis = false;
        this.peptideProphetRoc = new PeptideProphetROC();
        
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamReader.START_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase(MSMS_RUN_SUMMARY)) {
//                    System.out.println(reader.getAttributeValue(null, "base_name"));
                    break;
                }
                else if(reader.getLocalName().equalsIgnoreCase("analysis_summary")) {
                    if(reader.getAttributeValue(null,"analysis").equalsIgnoreCase("database_refresh")) {
                        refreshParserRun = true;
                    }
                }
                else if (reader.getLocalName().equalsIgnoreCase("peptideprophet_summary")) {
                    this.peptideProphetVersion = reader.getAttributeValue(null, "version");
                    inPPAnalysis = true;
                }
                else if (reader.getLocalName().equalsIgnoreCase("roc_data_point") && inPPAnalysis) {
                    // <roc_data_point min_prob="0.99" sensitivity="0.4384" error="0.0024" num_corr="1123" num_incorr="3"/>
                    PeptideProphetROCPoint rocPoint = new PeptideProphetROCPoint();
                    rocPoint.setMinProbability(Double.parseDouble(reader.getAttributeValue(null, "min_prob")));
                    rocPoint.setSensitivity(Double.parseDouble(reader.getAttributeValue(null, "sensitivity")));
                    rocPoint.setError(Double.parseDouble(reader.getAttributeValue(null, "error")));
                    rocPoint.setNumCorrect(Integer.parseInt(reader.getAttributeValue(null, "num_corr")));
                    rocPoint.setNumIncorrect(Integer.parseInt(reader.getAttributeValue(null, "num_incorr")));
                    this.peptideProphetRoc.addRocPoint(rocPoint);
                }
            }
        }
    }
    
    public boolean isRefreshParserRun() {
        return refreshParserRun;
    }
    
    public String getPeptideProphetVersion() {
        return this.peptideProphetVersion;
    }
    
    public PeptideProphetROC getPeptideProphetRoc() {
        return peptideProphetRoc;
    }

    @Override
    public void close() {
        if (reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
    }
    
    public void setDynamicResidueMods(List<MsResidueModificationIn> dynaResidueMods) {
        if (dynaResidueMods != null)
            this.searchDynamicResidueMods = dynaResidueMods;
    }
    
    @Override
    public boolean hasNextRunSearch() throws DataProviderException {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                int evtType = reader.getEventType();
                if (evtType == XMLStreamReader.START_ELEMENT) {
                    if (reader.getLocalName().equalsIgnoreCase(MSMS_RUN_SUMMARY)) {
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            if (reader.getAttributeLocalName(i).equalsIgnoreCase("base_name"))
                                currentRunSearchName = new File(reader.getAttributeValue(i)).getName();
                        }
                        if(this.searchDynamicResidueMods == null) {
                            readModifications();
                        }
                        reader.next();
                        return true;
                    }
                }
                reader.next();
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    
    private void readModifications() throws XMLStreamException {
        
        this.searchDynamicResidueMods = new ArrayList<MsResidueModificationIn>();
        while(reader.hasNext()) {
            int evtType = reader.next();
            // We should find the <aminoacid_modification> elements under the <search_summary> element
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_summary")) {
                return;
            }
            
            if (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("aminoacid_modification")) {
                // <aminoacid_modification aminoacid="M" massdiff="15.9949" mass="147.0354" variable="Y" symbol="*"/>
                // <aminoacid_modification aminoacid="C" massdiff="57.0215" mass="160.0306" variable="N"/>
                String variable = reader.getAttributeValue(null, "variable");
                // We are interested only in the dynamic modifications
                if("Y".equalsIgnoreCase(variable)) {
                    String aa = reader.getAttributeValue(null, "aminoacid");
                    String symbol = reader.getAttributeValue(null, "symbol");
                    String massdiff = reader.getAttributeValue(null, "massdiff");
                    ResidueModification mod = new ResidueModification();
                    mod.setModificationMass(new BigDecimal(massdiff));
                    mod.setModificationSymbol(symbol.charAt(0));
                    mod.setModifiedResidue(aa.charAt(0));
                    this.searchDynamicResidueMods.add(mod);
                }
            }
        }
    }
    
    @Override
    public MsRunSearchIn getSearchHeader() throws DataProviderException {
        RunSearchBean runSearch = new RunSearchBean();
        runSearch.setSearchFileFormat(SearchFileFormat.PEPXML);
        return runSearch;
    }
    
    @Override
    public String getFileName() {
        return currentRunSearchName;
    }

    @Override
    public boolean hasNextSearchScan() throws DataProviderException {
        if (reader == null)
            return false;
        try {
            while(reader.hasNext()) {
                
                int evtId = reader.next();
                if (evtId == XMLStreamReader.END_ELEMENT) {
                    if (reader.getLocalName().equals(MSMS_RUN_SUMMARY))  {
                        return false;
                    }
                }
                else if (evtId == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query")) {
                        return true;
                }
            }
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return false;
    }
    
    @Override
    public PepXmlSearchScanIn getNextSearchScan() throws DataProviderException {
        
        SearchScan scanResult = new SearchScan();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
//            if (attrib.equalsIgnoreCase("spectrum"))
//                scanResult.setSpectrumString(val);
            if (attrib.equalsIgnoreCase("start_scan"))
                scanResult.scanNumber = Integer.parseInt(val);
//            else if (attrib.equalsIgnoreCase("end_scan"))
//                scanResult.setEndScan(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("precursor_neutral_mass"))
                // NOTE: We store M+H in the database
                scanResult.precursorMass = new BigDecimal(val).add(BigDecimal.valueOf(AminoAcidUtils.PROTON));
            else if (attrib.equalsIgnoreCase("assumed_charge"))
                scanResult.charge = Integer.parseInt(val);
            else if (attrib.equalsIgnoreCase("retention_time_sec"))
                scanResult.retentionTime = new BigDecimal(val);
        }
        // read the search hits for this scan
        try {
            readHitsForScan(scanResult, (List<MsResidueModificationIn>) searchDynamicResidueMods);
        }
        catch (NumberFormatException e) {
            throw new DataProviderException("Error parsing number in file: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        }
        return scanResult;
    }
    
    private void readHitsForScan(SearchScan scanResult, List<MsResidueModificationIn> searchDynaResidueMods) 
        throws XMLStreamException, DataProviderException {
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("spectrum_query"))
                break;
            if (evtType == XMLStreamReader.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit")) {
                SequestPeptideProphetResultIn hit = readSearchHit(scanResult, searchDynaResidueMods);
                scanResult.addSearchHit(hit);
            }
        }  
    }
    
    private SequestPeptideProphetResultIn readSearchHit(SearchScan scan, List<MsResidueModificationIn> searchDynaResidueMods) 
            throws XMLStreamException, DataProviderException {
        
        ScanResult hit = new ScanResult();
        String peptideSeq = null;
        char preResidue = 0;
        char postResidue = 0;
        String prAcc = null;
        String prDescr = null;
        
        int numMatchingProteins = 0;
        int numEnzymaticTermini = 0;
        
        SequestResult seqRes = new SequestResult(searchDynaResidueMods);
        seqRes.setScanNumber(scan.getScanNumber());
        seqRes.setCharge(scan.getCharge());
        seqRes.setObservedMass(scan.getObservedMass());
        PeptideProphetResultBean ppRes = new PeptideProphetResultBean();
        ppRes.setCharge(scan.getCharge());
        ppRes.setObservedMass(scan.getObservedMass());
        
        // read the <search_hit> element
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            
            seqRes.setObservedMass(scan.getObservedMass());
            
            String attrib = reader.getAttributeLocalName(i);
            String val = reader.getAttributeValue(i);
            if (attrib.equalsIgnoreCase("hit_rank"))
                seqRes.setxCorrRank(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("peptide"))
                peptideSeq = val;
            else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                preResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                postResidue = Character.valueOf(val.charAt(0));
            else if (attrib.equalsIgnoreCase("protein"))
                prAcc = val;
            else if (attrib.equalsIgnoreCase("protein_descr"))
                prDescr = val;
            else if (attrib.equalsIgnoreCase("num_tot_proteins"))
                numMatchingProteins = Integer.parseInt(val);
            else if (attrib.equalsIgnoreCase("num_matched_ions"))
                seqRes.setNumMatchingIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("tot_num_ions"))
                seqRes.setNumPredictedIons(Integer.parseInt(val));
            else if (attrib.equalsIgnoreCase("calc_neutral_pep_mass")) {
                // NOTE: We are storing M+H in the database
                seqRes.setCalculatedMass(new BigDecimal(val).add(BigDecimal.valueOf(AminoAcidUtils.PROTON))); 
            }
            else if (attrib.equalsIgnoreCase("num_tol_term")) 
                numEnzymaticTermini = Integer.parseInt(val);
        }
        
        seqRes.setOriginalPeptideSequence(preResidue+"."+peptideSeq+"."+postResidue);
        DbLocus locus = new DbLocus(prAcc, prDescr);
        locus.setNtermResidue(preResidue);
        locus.setCtermResidue(postResidue);
        locus.setNumEnzymaticTermini(numEnzymaticTermini);
        seqRes.addMatchingLocus(locus);
        
        boolean inPeptideProphetAnalysis = false;
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit"))
                break;
            if(evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("peptideprophet_result"))
                inPeptideProphetAnalysis = false;
            
            if (evtType == XMLStreamReader.START_ELEMENT) {
                // read the <alternative_protein> elements
                if (reader.getLocalName().equalsIgnoreCase("alternative_protein")) {
                    prAcc = null;
                    prDescr = null;
                    preResidue = 0;
                    postResidue = 0;
                    numEnzymaticTermini = 0;
                    
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        String attrib = reader.getAttributeLocalName(i);
                        String val = reader.getAttributeValue(i);
                        if (attrib.equalsIgnoreCase("protein"))
                            prAcc = val;
                        else if (attrib.equalsIgnoreCase("protein_descr"))
                            prDescr = val;
                        else if (attrib.equalsIgnoreCase("peptide_prev_aa"))
                            preResidue = Character.valueOf(val.charAt(0));
                        else if (attrib.equalsIgnoreCase("peptide_next_aa"))
                            postResidue = Character.valueOf(val.charAt(0));
                        else if (attrib.equalsIgnoreCase("num_tol_term")) 
                            numEnzymaticTermini = Integer.parseInt(val);
                    }
                    locus = new DbLocus(prAcc, prDescr);
                    locus.setNtermResidue(preResidue);
                    locus.setCtermResidue(postResidue);
                    locus.setNumEnzymaticTermini(numEnzymaticTermini);
                    seqRes.addMatchingLocus(locus);
                }
                // read the <search_score> elements
                else if (reader.getLocalName().equalsIgnoreCase("search_score")) {
                    String scoreType = reader.getAttributeValue(null, "name");
                    String scoreVal = reader.getAttributeValue(null, "value");
                    if (scoreType.equalsIgnoreCase("xcorr"))
                        seqRes.setXcorr(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("deltacn"))
                        seqRes.setDeltaCN(new BigDecimal(scoreVal));
                    else if(scoreType.equalsIgnoreCase("deltacnstar"))
                        seqRes.setDeltaCNstar(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("spscore"))
                        seqRes.setSp(new BigDecimal(scoreVal));
                    else if (scoreType.equalsIgnoreCase("sprank"))
                        seqRes.setSpRank(Integer.parseInt(scoreVal));
                }
                
                // read the PeptideProphet scores
                else if (reader.getLocalName().equalsIgnoreCase("analysis_result")) {
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        // make sure we are looking at PeptideProphet analysis results
                        String attrib = reader.getAttributeLocalName(i);
                        String val = reader.getAttributeValue(i);
                        if (attrib.equalsIgnoreCase("analysis")) {
                            if("peptideprophet".equalsIgnoreCase(val)) {
                                break;
                            }
                            else {
                                throw new DataProviderException("Expected PeptideProphet analysis. Found: "+val);
                            }
                        }
                    }
                }
                else if (reader.getLocalName().equalsIgnoreCase("peptideprophet_result")) {
                    inPeptideProphetAnalysis = true;
                    String probability = reader.getAttributeValue(null, "probability");
                    String allNttProb = reader.getAttributeValue(null, "all_ntt_prob");
                    ppRes.setAllNttProb(allNttProb);
                    ppRes.setProbability(Double.parseDouble(probability));
                }
                // read the <parameter> elements (PeptideProphet scores)
                else if (reader.getLocalName().equalsIgnoreCase("parameter") && inPeptideProphetAnalysis) {
                    String scoreType = reader.getAttributeValue(null, "name");
                    String scoreVal = reader.getAttributeValue(null, "value");
                    if (scoreType.equalsIgnoreCase("fval"))
                        ppRes.setfVal(Double.parseDouble(scoreVal));
                    else if (scoreType.equalsIgnoreCase("ntt"))
                        ppRes.setNumEnzymaticTermini(Integer.parseInt(scoreVal));
                    else if (scoreType.equalsIgnoreCase("nmc"))
                        ppRes.setNumMissedCleavages(Integer.parseInt(scoreVal));
                    else if (scoreType.equalsIgnoreCase("massd"))
                        ppRes.setMassDifference(Double.parseDouble(scoreVal));
                }
            }
            
        } // end of parsing
        
        
        if (numMatchingProteins != seqRes.getProteinMatchList().size())
            throw new DataProviderException("value of attribute num_matched_ions("+numMatchingProteins+
                    ") does not match number of proteins("+seqRes.getProteinMatchList().size()+") found for this hit. "
                    +"Scan# "+scan.getScanNumber()+"; hit rank: "+seqRes.getSequestResultData().getxCorrRank());
        
        
        
        hit.seqRes = seqRes;
        hit.ppRes = ppRes;
        
        // make sure the probability for this hit is the best from all_ntt_prob
        if(ppRes.hasAllNttProb()) {
           int maxNet = 0;  // max number of enzymatic termini for a matched protein
           List<MsSearchResultProteinIn> proteins = hit.getProteinMatchList();
           for(MsSearchResultProteinIn protein: proteins) {
               maxNet = Math.max(maxNet, ((DbLocus)protein).getNumEnzymaticTermini());
           }
           if(maxNet == 1) 
               ppRes.setProbability(ppRes.getProbability() >= ppRes.getProbabilityNet_1() ? ppRes.getProbability() : ppRes.getProbabilityNet_1());
           else if (maxNet == 2)
               ppRes.setProbability(ppRes.getProbability() >= ppRes.getProbabilityNet_2() ? ppRes.getProbability() : ppRes.getProbabilityNet_2());
           
           ppRes.setNumEnzymaticTermini(maxNet);
        }
        
        return hit;
    }
    
    private class SearchScan implements PepXmlSearchScanIn {

        private int scanNumber;
        private int charge;
        private BigDecimal precursorMass;
        private BigDecimal retentionTime;
        private List<SequestPeptideProphetResultIn> results = new ArrayList<SequestPeptideProphetResultIn>();
        
        @Override
        public int getScanNumber() {
            return scanNumber;
        }

        @Override
        public int getCharge() {
            return charge;
        }

        @Override
        public BigDecimal getObservedMass() {
            return precursorMass;
        }

        @Override
        public BigDecimal getRetentionTime() {
            return retentionTime;
        }
        
        @Override
        public List<SequestPeptideProphetResultIn> getScanResults() {
            return results;
        }
        
        public void addSearchHit(SequestPeptideProphetResultIn result) {
            this.results.add(result);
        }
    }
    
    private class ScanResult implements SequestPeptideProphetResultIn {

        private PeptideProphetResultBean ppRes;
        private SequestResult seqRes;
        
        @Override
        public SequestResultData getSequestResultData() {
            return seqRes.getSequestResultData();
        }

        @Override
        public List<MsSearchResultProteinIn> getProteinMatchList() {
            return seqRes.getProteinMatchList();
        }

        @Override
        public int getScanNumber() {
            return seqRes.getScanNumber();
        }

        @Override
        public int getCharge() {
            return seqRes.getCharge();
        }

        @Override
        public BigDecimal getObservedMass() {
            return seqRes.getObservedMass();
        }

        @Override
        public MsSearchResultPeptide getResultPeptide() {
            return seqRes.getResultPeptide();
        }

        @Override
        public ValidationStatus getValidationStatus() {
            return seqRes.getValidationStatus();
        }

        @Override
        public double getProbabilityNet_0() {
            return ppRes.getProbabilityNet_0();
        }

        @Override
        public double getProbabilityNet_1() {
            return ppRes.getProbabilityNet_1();
        }

        @Override
        public double getProbabilityNet_2() {
            return ppRes.getProbabilityNet_2();
        }
        
        @Override
        public double getMassDifference() {
            return ppRes.getMassDifference();
        }

        @Override
        public int getNumMissedCleavages() {
            return ppRes.getNumMissedCleavages();
        }

        @Override
        public int getNumEnzymaticTermini() {
            return ppRes.getNumEnzymaticTermini();
        }

        @Override
        public double getProbability() {
            return ppRes.getProbability();
        }

        @Override
        public double getfVal() {
            return ppRes.getfVal();
        }
    }
}
