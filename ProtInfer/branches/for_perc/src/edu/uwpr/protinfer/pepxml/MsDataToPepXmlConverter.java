/**
 * MsDataToPepXmlConverter.java
 * @author Vagisha Sharma
 * Jan 17, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.pepxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public class MsDataToPepXmlConverter {

    private static final DAOFactory daofactory = DAOFactory.instance();
    private static final SequestSearchDAO seqSearchDao = daofactory.getSequestSearchDAO();
    private static final ProlucidSearchDAO plcidSearchDao = daofactory.getProlucidSearchDAO();
    private static final SQTRunSearchDAO runSearchDao = daofactory.getSqtRunSearchDAO();
    private static final MsRunDAO runDao = daofactory.getMsRunDAO();
    private static final MsScanDAO scanDao = daofactory.getMsScanDAO();
    private static final SQTSearchScanDAO sqtScanDao = daofactory.getSqtSpectrumDAO();
    private static final SequestSearchResultDAO resultDao = daofactory.getSequestResultDAO();
    
    private static final Logger log = Logger.getLogger(MsDataToPepXmlConverter.class);
    
    private List<MsResidueModification> staticMods;
    
    
    public boolean convertSearch(int searchId, String outfile) throws FileNotFoundException, XMLStreamException, DatatypeConfigurationException {
        
        SequestSearch search = seqSearchDao.loadSearch(searchId);
        staticMods = search.getStaticResidueMods();
        
        if(search == null) {
            log.error("No search found with ID: "+searchId);
            return false;
        }
        
        XMLStreamWriter writer = initDocument(outfile);
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);
        
        for(int id: runSearchIds) {
            SQTRunSearch runSearch = runSearchDao.loadRunSearch(id);
            writeRunSearch(search, runSearch, writer);
        }
        
        endDocument(writer);
        writer.close();
        return true;
    }
    
    public boolean convertRunSearch(int runSearchId, String outdir) throws FileNotFoundException, XMLStreamException, DatatypeConfigurationException {
        
        SQTRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("RunSearch with ID "+runSearchId+" not found!");
            return false;
        }
        
        SequestSearch search = seqSearchDao.loadSearch(runSearch.getSearchId());
        this.staticMods = search.getStaticResidueMods();
        
        String outfile = runSearchDao.loadFilenameForRunSearch(runSearchId)+".pep.xml";
        outfile = outdir+File.separator+outfile;
        
        XMLStreamWriter writer = initDocument(outfile);
        writeRunSearch(search, runSearch, writer);
        
        endDocument(writer);
        writer.close();
        return true;
    }
    
    private XMLStreamWriter initDocument(String outfile) throws XMLStreamException, FileNotFoundException, DatatypeConfigurationException {
        OutputStream out = new FileOutputStream(outfile);
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
        
        writeHeaders(writer);
        startDocument(writer, outfile);
        return writer;
    }
    
    private void writeHeaders(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        newLine(writer);
        writer.writeDTD("<?xml-stylesheet type=\"text/xsl\" href=\"http://regis-web.systemsbiology.net/pepXML_std.xsl\"?>");
        newLine(writer);
    }
    
    private void startDocument(XMLStreamWriter writer, String outFilePath) throws XMLStreamException, DatatypeConfigurationException {
        writer.writeStartElement("msms_pipeline_analysis");
        DatatypeFactory df;
        df = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
        writer.writeAttribute("date", calendar.toXMLFormat());
        writer.writeAttribute("summary_xml", outFilePath);
        
        //writer.writeAttribute("xmlns", "http://regis-web.systemsbiology.net/pepXML");
        //writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        //writer.writeAttribute("xsi:schemaLocation", "http://regis-web.systemsbiology.net/pepXML /net/pr/vol1/ProteomicsResource/bin/TPP/bin/20080417-TPP_v3.5.3/schema/pepXML_v110.xsd");
        newLine(writer);
    }
    
    private void endDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
        writer.writeEndDocument();
        writer.close();
    }
    
    
    private void writeRunSearch(SequestSearch search, SQTRunSearch runSearch, XMLStreamWriter writer) 
                throws FileNotFoundException, XMLStreamException {
        
        String basefile = runSearchDao.loadFilenameForRunSearch(runSearch.getId());
        writer.writeStartElement("msms_run_summary");
        writer.writeAttribute("base_name", basefile);
        
        MsRun run = runDao.loadRun(runSearch.getRunId());
        if(run == null) {
            log.error("No run found with ID: "+runSearch.getRunId());
            throw new IllegalArgumentException("No run found with ID: "+runSearch.getRunId());
        }
        writer.writeAttribute("raw_data_type", "."+run.getRunFileFormat().name().toLowerCase());
        writer.writeAttribute("raw_data", "."+run.getRunFileFormat().name().toLowerCase());
        
        //writer.writeAttribute("msManufacturer", "Thermo Finnigan");
        newLine(writer);
        
        // write enzyme information; "sample_enzyme" element
        writeEnzymes(search, writer);
        
        // write search summary; "search_summary" element
        writeSearchSummary(search, runSearch, writer, basefile);
        
        writeSearchResults(runSearch, writer, basefile);
        writer.writeEndElement();
        newLine(writer);
    }

    private void writeSearchSummary(SequestSearch search,
            SQTRunSearch runSearch, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        // search summary
        startSearchSummary(search, runSearch, writer, basefile);
        
        // search database
        writeSearchDatabase(search, writer);
        
        if(search.getEnzymeList().size() > 0) {
            MsEnzyme enzyme = search.getEnzymeList().get(0);
            writer.writeStartElement("enzymatic_search_constraint");
            writer.writeAttribute("enzyme", enzyme.getName());
            String maxNumIntClv = getMaxNumInternalClevages(search.getId());
            if(maxNumIntClv != null)
                writer.writeAttribute("max_num_internal_cleavages", maxNumIntClv);
            writer.writeEndElement();
            newLine(writer);
            
        }
        // dynamic modifications
        //<aminoacid_modification aminoacid="M" massdiff="15.9990" mass="147.1916" variable="Y" symbol="*"/>
        List<MsResidueModification> dynamods = search.getDynamicResidueMods();
        for(MsResidueModification mod: dynamods) {
            writer.writeStartElement("aminoacid_modification");
            writer.writeAttribute("aminoacid", String.valueOf(mod.getModifiedResidue()));
            writer.writeAttribute("massdiff", mod.getModificationMass().toString());
            writer.writeAttribute("mass", mod.getModificationMass().toString());
            writer.writeAttribute("variable", "Y");
            writer.writeAttribute("symbol", String.valueOf(mod.getModificationSymbol()));
            writer.writeEndElement();
            newLine(writer);
        }
        //<aminoacid_modification aminoacid="C" massdiff="57.0210" mass="160.1598" variable="N"/>
        List<MsResidueModification> staticmods = search.getStaticResidueMods();
        for(MsResidueModification mod: staticmods) {
            writer.writeStartElement("aminoacid_modification");
            writer.writeAttribute("aminoacid", String.valueOf(mod.getModifiedResidue()));
            writer.writeAttribute("massdiff", mod.getModificationMass().toString());
            writer.writeAttribute("mass", mod.getModificationMass().toString());
            writer.writeAttribute("variable", "N");
            writer.writeEndElement();
            newLine(writer);
        }
        
        // write the SEQUEST Parameters
        List<SequestParam> params = search.getSequestParams();
        for(SequestParam param: params) {
            writer.writeStartElement("parameter");
            writer.writeAttribute("name", param.getParamName());
            writer.writeAttribute("value", param.getParamValue());
            writer.writeEndElement();
            newLine(writer);
        }
        
        endSearchSummary(writer);
    }

    private String getMaxNumInternalClevages(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    private void writeSearchDatabase(SequestSearch search,
            XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("search_database");
        List<MsSearchDatabase> dbs = search.getSearchDatabases();
        writer.writeAttribute("local_path", dbs.get(0).getDatabaseFileName());
        writer.writeAttribute("type", "AA");
        writer.writeEndElement();
        newLine(writer);
    }

    private void startSearchSummary(SequestSearch search,
            SQTRunSearch runSearch, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        writer.writeStartElement("search_summary");
        writer.writeAttribute("base_name", basefile);
        writer.writeAttribute("search_engine", search.getSearchProgram().toString());
        
        writer.writeAttribute("precursor_mass_type", "monoisotopic");
        writer.writeAttribute("fragment_mass_type", "average");
        writer.writeAttribute("search_id", String.valueOf(runSearch.getId()));
        newLine(writer);
    }
    
    private void endSearchSummary(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }
    
    private String getPrecursorMassType(MsSearch search) {
        
    }

    private void writeEnzymes(MsSearch search, XMLStreamWriter writer)
            throws XMLStreamException {
        // write enzymes used
        List<MsEnzyme> enzymes = search.getEnzymeList();
        if (enzymes.size() > 0) {
            for(MsEnzyme enz: enzymes) {
                writer.writeStartElement("sample_enzyme");
                writer.writeAttribute("name", enz.getName());
                newLine(writer);
                // <specificity cut="KR" no_cut="P" sense="C"/>
                writer.writeStartElement("specificity");
                writer.writeAttribute("cut", enz.getCut());
                if(enz.getNocut() != null && enz.getNocut().length() > 0)
                    writer.writeAttribute("no_cut", enz.getNocut());
                writer.writeAttribute("sense", enz.getSense().getShortVal() == Sense.NTERM.getShortVal() ? "N" : "C");
                writer.writeEndElement();
                newLine(writer);
                
                writer.writeEndElement();
                newLine(writer);
            }
        }
    }

    private void writeSearchResults(SQTRunSearch runSearch, XMLStreamWriter writer, String filename) throws XMLStreamException {
        
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearch.getId());
        List<SequestSearchResult> results = new ArrayList<SequestSearchResult>(resultIds.size());
        for(Integer resId: resultIds) {
            SequestSearchResult res = resultDao.load(resId);
            results.add(res);
        }
        
        // order by scanID and then by charge
        Collections.sort(results, new Comparator<SequestSearchResult>() {
            @Override
            public int compare(SequestSearchResult o1, SequestSearchResult o2) {
                if (o1.getScanId() < o2.getScanId())    return -1;
                if (o1.getScanId() > o2.getScanId())    return 1;
                else {
                    return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
                }
            }});
        
        int lastScanId = -1;
        int lastCharge = -1;
        int index = 1;
        List<SequestSearchResult> resForScan = new ArrayList<SequestSearchResult>();
        for(SequestSearchResult result: results) {
            if(result.getScanId() != lastScanId || result.getCharge() != lastCharge) {
                if(resForScan.size() > 0) {
                    int scanNumber = scanDao.load(lastScanId).getStartScanNum();
                    SQTSearchScan sqtScan = sqtScanDao.load(runSearch.getId(), lastScanId, lastCharge);
                    writeResultsForScanCharge(resForScan, writer, index, filename, sqtScan);
                    index++;
                }
                resForScan.clear();
                lastScanId = result.getScanId();
                lastCharge = result.getCharge();
            }
            resForScan.add(result);
        }
        SQTSearchScan sqtScan = sqtScanDao.load(runSearch.getId(), lastScanId, lastCharge);
        writeResultsForScanCharge(resForScan, writer, index, filename, sqtScan);
    }


    private void writeResultsForScanCharge(List<SequestSearchResult> resForScanCharge, XMLStreamWriter writer, 
            int index, String filename, SQTSearchScan sqtScan) 
        throws XMLStreamException {
        
        MsScan scan = scanDao.load(sqtScan.getScanId());
        int scanNumber = scan.getStartScanNum();
        int charge = sqtScan.getCharge();
        BigDecimal mass = scan.getPrecursorMz();
        BigDecimal rt = scan.getRetentionTime();
        
        writer.writeStartElement("spectrum_query");
        String spectrum = filename+"."+scanNumber+"."+scanNumber+"."+charge;
        writer.writeAttribute("spectrum", spectrum);
        writer.writeAttribute("start_scan", String.valueOf(scanNumber));
        writer.writeAttribute("end_scan", String.valueOf(scanNumber));
        writer.writeAttribute("precursor_neutral_mass", mass.toString());
        writer.writeAttribute("assumed_charge", String.valueOf(charge));
        if(rt != null)
            writer.writeAttribute("retention_time_sec", rt.toString());
        writer.writeAttribute("index", String.valueOf(index));
        newLine(writer);
        
        // sort results by rank
        Collections.sort(resForScanCharge, new Comparator<SequestSearchResult>() {
            @Override
            public int compare(SequestSearchResult o1, SequestSearchResult o2) {
                return Integer.valueOf(o1.getSequestResultData().getxCorrRank()).compareTo(o2.getSequestResultData().getxCorrRank());
            }});
        for(SequestSearchResult result: resForScanCharge) {
            SequestResultData resData = result.getSequestResultData();
            MsSearchResultPeptide peptide = result.getResultPeptide();
            List<MsSearchResultProtein> proteins = result.getProteinMatchList();
            // accession strings may be separated by ^A; Calculate the number of proteins
            int numProteins = 0;
            for(MsSearchResultProtein protein: proteins) {
                String[] accessionStrings = protein.getAccession().split("\\cA");
                numProteins += accessionStrings.length;
            }
            
            writer.writeStartElement("search_hit");
            writer.writeAttribute("hit_rank", String.valueOf(resData.getxCorrRank()));
            writer.writeAttribute("peptide", peptide.getPeptideSequence());
            writer.writeAttribute("peptide_prev_aa", String.valueOf(peptide.getPreResidue()));
            writer.writeAttribute("peptide_next_aa", String.valueOf(peptide.getPostResidue()));
            writer.writeAttribute("protein", proteins.get(0).getAccession());
            writer.writeAttribute("num_tot_proteins", String.valueOf(numProteins));
            writer.writeAttribute("num_matched_ions", String.valueOf(resData.getMatchingIons()));
            writer.writeAttribute("tot_num_ions", String.valueOf(resData.getPredictedIons()));
//            writer.writeAttribute("calc_neutral_pep_mass", mass.toString());
//            writer.writeAttribute("massdiff", "0.0");
//            writer.writeAttribute("num_tol_term", "1");
//            writer.writeAttribute("num_missed_cleavages", "2");
//            writer.writeAttribute("is_rejected", "0");
//            writer.writeAttribute("protein_descr", "");
            newLine(writer);
            
            // write all the other proteins
            for(int i = 1; i < proteins.size(); i++) {
                String[] accessionStrings = proteins.get(i).getAccession().split("\\cA");
                for(String acc: accessionStrings) {
                    writer.writeStartElement("alternative_protein");
                    writer.writeAttribute("protein", acc);
//                    writer.writeAttribute("protein_descr", "");
//                    writer.writeAttribute("num_tol_term", "1");
                    writer.writeAttribute("peptide_prev_aa", String.valueOf(peptide.getPreResidue()));
                    writer.writeAttribute("peptide_next_aa", String.valueOf(peptide.getPostResidue()));
                    writer.writeEndElement();
                    newLine(writer);
                }
            }
            
            // write modifications
            writeModificationInfo(writer, peptide);
            
            // write all the scores for this result
            writer.writeStartElement("search_score");
            writer.writeAttribute("name", "xcorr");
            writer.writeAttribute("value", resData.getxCorr().toString());
            writer.writeEndElement();
            newLine(writer);
            writer.writeCharacters("\t\t");
            writer.writeStartElement("search_score");
            writer.writeAttribute("name", "deltacn");
            writer.writeAttribute("value", resData.getDeltaCN().toString());
            writer.writeEndElement();
            newLine(writer);
            writer.writeCharacters("\t\t");
            writer.writeStartElement("search_score");
            writer.writeAttribute("name", "spscore");
            writer.writeAttribute("value", resData.getSp().toString());
            writer.writeEndElement();
            newLine(writer);
            writer.writeCharacters("\t\t");
            writer.writeStartElement("search_score");
            writer.writeAttribute("name", "sprank");
            writer.writeAttribute("value", String.valueOf(resData.getSpRank()));
            writer.writeEndElement();
            newLine(writer);
            
            
            writer.writeCharacters("\t");
            writer.writeEndElement();
            newLine(writer);
        }
        writer.writeEndElement();
        newLine(writer);
    }

    private void writeModificationInfo(XMLStreamWriter writer, MsSearchResultPeptide peptide) throws XMLStreamException {
        
        
        // get the dynamic mods
        List<MsResultResidueMod> resultDynaMods = peptide.getResultDynamicResidueModifications();
        
        // get the static mods
        Map<Integer, Double> resultStaticMods = new HashMap<Integer, Double>();
        String seq = peptide.getPeptideSequence();
        for(MsResidueModification mod: this.staticMods) {
            char modChar = mod.getModifiedResidue();
            double mass = Math.round(mod.getModificationMass().doubleValue() +
                    AminoAcidUtils.avgMass(modChar));
            int s = 0;
            int idx = -1;
            while((idx = seq.indexOf(modChar, s)) != -1) {
                resultStaticMods.put(idx, mass);
                s = idx+1;
            }
        }
        
        // If there are no modifications don't write anything
        if(resultDynaMods.size() == 0 && resultStaticMods.size() == 0)
            return;
        
        writer.writeStartElement("modification_info");
        writer.writeAttribute("modified_peptide", peptide.getModifiedPeptide());
        newLine(writer);
        
        for(MsResultResidueMod mod: resultDynaMods) {
            writer.writeStartElement("mod_aminoacid_mass");
            writer.writeAttribute("position", String.valueOf(mod.getModifiedPosition()+1));
            double modMass = Math.round(mod.getModificationMass().doubleValue() +
                    AminoAcidUtils.avgMass(seq.charAt(mod.getModifiedPosition())));
            writer.writeAttribute("mass", String.valueOf(modMass));
            writer.writeEndElement();
            newLine(writer);
        }
        
        for(Integer pos: resultStaticMods.keySet()) {
            writer.writeStartElement("mod_aminoacid_mass");
            writer.writeAttribute("position", String.valueOf(pos+1));
            writer.writeAttribute("mass", String.valueOf(resultStaticMods.get(pos)));
            writer.writeEndElement();
            newLine(writer);
        }
        
        writer.writeEndElement();
        newLine(writer);
    }


    private void newLine(XMLStreamWriter writer ) throws XMLStreamException {
        writer.writeCharacters("\n");
    }
    

    public static void main (String[] args) throws FileNotFoundException, XMLStreamException {
        String outdir = "/Users/silmaril/WORK/UW/PROT_INFER/TEST_DATA/runID3136_exptID90/phos/pepxml";
        int searchId = 3;
        
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);
        MsDataToPepXmlConverter converter = new MsDataToPepXmlConverter();
        for(Integer id: runSearchIds)
            converter.convertRunSearch(id, outdir);
    }
}
