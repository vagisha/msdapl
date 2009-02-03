/**
 * PepXmlConverter.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.pepxml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public abstract class PepXmlConverter {

    
    static final DAOFactory daofactory = DAOFactory.instance();
    static final MsRunDAO runDao = daofactory.getMsRunDAO();
    static final MsScanDAO scanDao = daofactory.getMsScanDAO();
    static final MsRunSearchDAO runSearchDao = daofactory.getMsRunSearchDAO();
    static final MsSearchResultDAO resultDao = daofactory.getMsSearchResultDAO();
    static final SQTSearchScanDAO sqtScanDao = daofactory.getSqtSpectrumDAO();
    
    private static final Logger log = Logger.getLogger(PepXmlConverter.class);
    
    private MassType fragmentMassType;
    
    XMLStreamWriter initDocument(String outfile) throws XMLStreamException, FileNotFoundException, DatatypeConfigurationException {
        OutputStream out = new FileOutputStream(outfile);
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
        
        writeHeaders(writer);
        return writer;
    }
    
    private void writeHeaders(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        newLine(writer);
        writer.writeDTD("<?xml-stylesheet type=\"text/xsl\" href=\"http://regis-web.systemsbiology.net/pepXML_std.xsl\"?>");
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // ms_ms_pipeline_analysis
    //-------------------------------------------------------------------------------------------
    void startMsmsPipelineAnalysis(XMLStreamWriter writer, String outFilePath) throws XMLStreamException, DatatypeConfigurationException {
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
    
    void endMsmsPipelineAnalysis(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
        writer.writeEndDocument();
        writer.close();
    }
    
    //-------------------------------------------------------------------------------------------
    // ms_ms_run_summary
    //-------------------------------------------------------------------------------------------
    void startMsmsRunSummary(int runSearchId, XMLStreamWriter writer) throws XMLStreamException {

        String basefile = runSearchDao.loadFilenameForRunSearch(runSearchId);
        writer.writeStartElement("msms_run_summary");
        writer.writeAttribute("base_name", basefile);

        MsRun run = runDao.loadRun(runSearchId);
        if(run == null) {
            log.error("No run found with ID: "+runSearchId);
            throw new IllegalArgumentException("No run found with ID: "+runSearchId);
        }
        writer.writeAttribute("raw_data_type", "."+run.getRunFileFormat().name().toLowerCase());
        writer.writeAttribute("raw_data", "."+run.getRunFileFormat().name().toLowerCase());

        newLine(writer);
    }
    
    void endMsmsRunSummary(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // enzymes
    //-------------------------------------------------------------------------------------------
    void writeEnzymes(MsSearch search, XMLStreamWriter writer)
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
    
    
    //-------------------------------------------------------------------------------------------
    // search_summary
    //-------------------------------------------------------------------------------------------
    void writeSearchSummary(MsSearch search,
            MsRunSearch runSearch, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        
        // search summary
        startSearchSummary(search, runSearch, writer, basefile);
        
        // search database
        writeSearchDatabase(search, writer);
        
        // write enzymatic search constraint
        writeEnzymaticSearchConstraints(search, writer);
        
        // dynamic modifications
        writeModifications(search, writer);
        
        // subclass can write any program specif parameters
        wirteProgramSpecificParams(search.getId(), writer);
        
        endSearchSummary(writer);
    }

    private void writeModifications(MsSearch search, XMLStreamWriter writer)
            throws XMLStreamException {
        //<aminoacid_modification aminoacid="M" massdiff="15.9990" mass="147.1916" variable="Y" symbol="*"/>
        List<MsResidueModification> dynamods = search.getDynamicResidueMods();
        for(MsResidueModification mod: dynamods) {
            writeResidueModification(mod, true, writer);
        }
        //<aminoacid_modification aminoacid="C" massdiff="57.0210" mass="160.1598" variable="N"/>
        List<MsResidueModification> staticmods = search.getStaticResidueMods();
        for(MsResidueModification mod: staticmods) {
            writeResidueModification(mod, false, writer);
        }
        
        // dynamic terminal modification
        List<MsTerminalModification> dynaTermMods = search.getDynamicTerminalMods();
        for(MsTerminalModification mod: dynaTermMods) {
            writeTerminalModification(mod, true, writer);
        }
        
        // dynamic residue modification
        List<MsTerminalModification> staticTermMods = search.getStaticTerminalMods();
        for(MsTerminalModification mod: staticTermMods) {
            writeTerminalModification(mod, false, writer);
        }
        
    }
    
    private void writeResidueModification(MsResidueModification mod, boolean dynamic, XMLStreamWriter writer) throws XMLStreamException {
        
        writer.writeStartElement("aminoacid_modification");
        writer.writeAttribute("aminoacid", String.valueOf(mod.getModifiedResidue()));
        
        double massDiff = mod.getModificationMass().doubleValue();
        String massDiffStr = massDiff < 0 ? "-"+massDiff : "+"+massDiff;
        writer.writeAttribute("massdiff", massDiffStr);
        
        double aaMass = massDiff;
        if(fragmentMassType == MassType.AVG)
            aaMass += AminoAcidUtils.avgMass(mod.getModifiedResidue());
        else
            aaMass += AminoAcidUtils.monoMass(mod.getModifiedResidue());
        writer.writeAttribute("mass", String.valueOf(aaMass));
        
        if(dynamic)
            writer.writeAttribute("variable", "Y");
        else
            writer.writeAttribute("variable", "N");
        
        if(mod.getModificationSymbol() != MsResidueModification.EMPTY_CHAR) {
            writer.writeAttribute("symbol", String.valueOf(mod.getModificationSymbol()));
        }
        writer.writeEndElement();
        newLine(writer);
    }
    
    private void writeTerminalModification(MsTerminalModification mod, boolean dynamic, XMLStreamWriter writer) throws XMLStreamException {
        
        writer.writeStartElement("terminal_modification");
        
        //from pepXML schema: n for N-terminus, c for C-terminus
        writer.writeAttribute("terminus", String.valueOf(mod.getModifiedTerminal().toChar()).toLowerCase());
        
        double massDiff = mod.getModificationMass().doubleValue();
        String massDiffStr = massDiff < 0 ? "-"+massDiff : "+"+massDiff;
        writer.writeAttribute("massdiff", massDiffStr);
        
        // TODO from pepXML schema: Mass difference with respect to unmodified terminus
        // Not sure how to calculate this
        writer.writeAttribute("mass", massDiffStr);
        
        if(dynamic)
            writer.writeAttribute("variable", "Y");
        else
            writer.writeAttribute("variable", "N");
        
        if(mod.getModificationSymbol() != MsResidueModification.EMPTY_CHAR) {
            writer.writeAttribute("symbol", String.valueOf(mod.getModificationSymbol()));
        }
        writer.writeEndElement();
        newLine(writer);
    }

    private void writeEnzymaticSearchConstraints(MsSearch search,
            XMLStreamWriter writer) throws XMLStreamException {
        if(search.getEnzymeList().size() > 0) {
            MsEnzyme enzyme = search.getEnzymeList().get(0);
            writer.writeStartElement("enzymatic_search_constraint");
            writer.writeAttribute("enzyme", enzyme.getName());
            String maxNumIntClv = getMaxNumInternalClevages(search.getId());
            if(maxNumIntClv != null)
                writer.writeAttribute("max_num_internal_cleavages", maxNumIntClv);
            String minNumTermini = getMinNumTermini(search.getId());
            if(minNumTermini != null)
                writer.writeAttribute("min_number_termini", minNumTermini);
            writer.writeEndElement();
            newLine(writer);
        }
    }
    
    private void writeSearchDatabase(MsSearch search,
            XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("search_database");
        List<MsSearchDatabase> dbs = search.getSearchDatabases();
        writer.writeAttribute("local_path", dbs.get(0).getDatabaseFileName());
        writer.writeAttribute("type", "AA");
        writer.writeEndElement();
        newLine(writer);
    }

    private void startSearchSummary(MsSearch search,
            MsRunSearch runSearch, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        writer.writeStartElement("search_summary");
        writer.writeAttribute("base_name", basefile);
        writer.writeAttribute("search_engine", search.getSearchProgram().toString());
        
        // mass type used for the search
        String pmt = getPrecursorMassType(search.getId()) == MassType.MONO ? "monoisotopic" : "average";
        writer.writeAttribute("precursor_mass_type", pmt);
        this.fragmentMassType = getFragmentMassType(search.getId());
        pmt = fragmentMassType == MassType.MONO ? "monoisotopic" : "average";
        
        writer.writeAttribute("fragment_mass_type", pmt);
        writer.writeAttribute("search_id", String.valueOf(runSearch.getId()));
        newLine(writer);
    }
    
    private void endSearchSummary(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
        newLine(writer);
    }
    
    //-------------------------------------------------------------------------------------------
    // spectrum_query
    //-------------------------------------------------------------------------------------------
    private void writeSearchResults(MsRunSearch runSearch, XMLStreamWriter writer, String basefile) throws XMLStreamException {
        
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearch.getId());
        List<SequestSearchResult> results = new ArrayList<SequestSearchResult>(resultIds.size());
        for(Integer resId: resultIds) {
            SequestSearchResult res = resultDao.load(resId);
            results.add(res);
        }
        
        int lastScanId = -1;
        int lastCharge = -1;
        int index = 1;
        List<SequestSearchResult> resForScan = new ArrayList<SequestSearchResult>();
        for(SequestSearchResult result: results) {
            if(result.getScanId() != lastScanId || result.getCharge() != lastCharge) {
                if(resForScan.size() > 0) {
                    int scanNumber = scanDao.load(lastScanId).getStartScanNum();
                    SQTSearchScan sqtScan = sqtScanDao.load(runSearch.getId(), lastScanId, lastCharge);
                    writeResultsForScanCharge(resForScan, writer, index, basefile, sqtScan);
                    index++;
                }
                resForScan.clear();
                lastScanId = result.getScanId();
                lastCharge = result.getCharge();
            }
            resForScan.add(result);
        }
        SQTSearchScan sqtScan = sqtScanDao.load(runSearch.getId(), lastScanId, lastCharge);
        writeResultsForScanCharge(resForScan, writer, index, basefile, sqtScan);
    }
    
    //-------------------------------------------------------------------------------------------
    // TO BE IMPLEMENTED BY SUBCLASSES
    //-------------------------------------------------------------------------------------------
    abstract MassType getPrecursorMassType(int searchId);
    
    abstract MassType getFragmentMassType(int searchId);
    
    abstract String getMaxNumInternalClevages(int id);
    
    abstract String getMinNumTermini(int id);
    
    abstract void wirteProgramSpecificParams(int id, XMLStreamWriter writer) throws XMLStreamException;
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    
    void newLine(XMLStreamWriter writer ) throws XMLStreamException {
        writer.writeCharacters("\n");
    }
    
}
