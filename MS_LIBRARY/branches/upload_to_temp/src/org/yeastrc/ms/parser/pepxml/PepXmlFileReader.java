/**
 * PepXmlFileReader.java
 * @author Vagisha Sharma
 * Sep 13, 2009
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

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.PepxmlDataProvider;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResult;
import org.yeastrc.ms.util.AminoAcidUtils;


/**
 * 
 */
public class PepXmlFileReader implements PepxmlDataProvider<PepXmlSearchScanIn> {

    private static final String MSMS_RUN_SUMMARY = "msms_run_summary";
    private String filePath;
    private XMLStreamReader reader = null;
    private List<MsResidueModificationIn> searchDynamicResidueMods;
    private boolean refreshParserRun = false;
    
    private boolean parseEvalue = false;
    private int searchId = -1;
    
    private static final Logger log = Logger.getLogger(PepXmlFileReader.class.getName());
    
    public void setParseEvalue(boolean parseEvalue) {
        this.parseEvalue = parseEvalue;
    }
    
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
        
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamReader.START_ELEMENT) {
                if(reader.getLocalName().equalsIgnoreCase(MSMS_RUN_SUMMARY)) {
                    readModifications();
                    return;
                }
                else if(reader.getLocalName().equalsIgnoreCase("analysis_summary")) {
                    if(reader.getAttributeValue(null,"analysis").equalsIgnoreCase("database_refresh")) {
                        refreshParserRun = true;
                    }
                }
            }
        }
    }
    
    public boolean isRefreshParserRun() {
        return refreshParserRun;
    }
    
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
    
    @Override
    public void close() {
        if (reader != null) try {
            reader.close();
        }
        catch (XMLStreamException e) {}
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
        return new File(this.filePath).getName();
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
        
        SequestPeptideProphetResultBean hit = new SequestPeptideProphetResultBean(); 
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
        
        hit.setStrippedSequence(peptideSeq);
        hit.setPreResidue(preResidue);
        hit.setPostResidue(postResidue);
        
        
        DbLocus locus = new DbLocus(prAcc, prDescr);
        locus.setNtermResidue(preResidue);
        locus.setCtermResidue(postResidue);
        locus.setNumEnzymaticTermini(numEnzymaticTermini);
        seqRes.addMatchingProteinMatch(locus);
        
        
        while(reader.hasNext()) {
            int evtType = reader.next();
            if (evtType == XMLStreamReader.END_ELEMENT && reader.getLocalName().equalsIgnoreCase("search_hit"))
                break;
            
            if (evtType == XMLStreamReader.START_ELEMENT) {
                
                // read the modification information
                if(reader.getLocalName().equalsIgnoreCase("modification_info")) {
//                    modifiedSequence = reader.getAttributeValue(null, "modified_peptide");
                }
                if(reader.getLocalName().equalsIgnoreCase("mod_aminoacid_mass")) {
                    int pos = Integer.parseInt(reader.getAttributeValue(null, "position"));
                    BigDecimal mass = new BigDecimal(reader.getAttributeValue(null, "mass"));
                    // Add only if this is a dynamic residue modification   
                    if(isDynamicModification(peptideSeq.charAt(pos - 1), mass)) {
                        hit.addModification(new Modification(pos, mass));
                    }
                }
                
                // read the <alternative_protein> elements
                else if (reader.getLocalName().equalsIgnoreCase("alternative_protein")) {
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
                    seqRes.addMatchingProteinMatch(locus);
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
                    else if (scoreType.equalsIgnoreCase("spscore")) {
                        if(!parseEvalue)
                            seqRes.setSp(new BigDecimal(scoreVal));
                        else
                            seqRes.setEvalue(Double.valueOf(scoreVal));
                    }
                    else if (scoreType.equalsIgnoreCase("sprank"))
                        seqRes.setSpRank(Integer.parseInt(scoreVal));
                }
            }
            
        } // end of parsing
        
        
        if (numMatchingProteins != seqRes.getProteinMatchList().size()) {
//            log.warn("value of attribute num_tot_proteins("+numMatchingProteins+
//                    ") does not match number of proteins("+seqRes.getProteinMatchList().size()+") found for this hit. "
//                    +"Scan# "+scan.getScanNumber()+"; hit rank: "+seqRes.getSequestResultData().getxCorrRank());
//            throw new DataProviderException("value of attribute num_tot_proteins("+numMatchingProteins+
//                    ") does not match number of proteins("+seqRes.getProteinMatchList().size()+") found for this hit. "
//                    +"Scan# "+scan.getScanNumber()+"; hit rank: "+seqRes.getSequestResultData().getxCorrRank());
        
        }
        
        hit.setSequestResult(seqRes);
        
        return hit;
    }
    
    private boolean isDynamicModification(char modChar, BigDecimal mass) throws DataProviderException {
        boolean foundchar = false;
        for(MsResidueModificationIn mod: this.searchDynamicResidueMods) {
            if(mod.getModifiedResidue() == modChar) {
                foundchar = true;
                double massDiff = mass.doubleValue() - AminoAcidUtils.monoMass(modChar);
                if(Math.abs(massDiff - mod.getModificationMass().doubleValue()) < 0.05) {
                    return true;
                }
            }
        }
        if(foundchar) {
            throw new DataProviderException("Found a match for modified residue: "+modChar+
                    " but no match for mass: "+mass.doubleValue());
        }
        return false;
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
    
    public static void main(String[] args) throws DataProviderException {
        String file = "/Users/silmaril/Desktop/18mix_new/JE102306_102306_18Mix4_Tube1_01.pep.xml";
        PepXmlFileReader reader = new PepXmlFileReader();
        reader.open(file);
        System.out.println(reader.getFileName());
        reader.getSearchHeader();
        int numScans = 0;
        int numResults = 0;
        while(reader.hasNextSearchScan()) {
            PepXmlSearchScanIn scan = reader.getNextSearchScan();
            for(SequestPeptideProphetResultIn result: scan.getScanResults()) {
                numResults++;
            }
            numScans++;
        }
        System.out.println("NumScans read: "+numScans);
        System.out.println("Num results: "+numResults);
    }
}
