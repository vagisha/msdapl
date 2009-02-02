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
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;

/**
 * 
 */
public abstract class PepXmlConverter {

    
    static final DAOFactory daofactory = DAOFactory.instance();
    static final MsRunDAO runDao = daofactory.getMsRunDAO();
    static final MsScanDAO scanDao = daofactory.getMsScanDAO();
    static final MsRunSearchDAO runSearchDao = daofactory.getMsRunSearchDAO();
    static final SQTSearchScanDAO sqtScanDao = daofactory.getSqtSpectrumDAO();
    
    private static final Logger log = Logger.getLogger(PepXmlConverter.class);
    
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
    
    void writeSearchSummary(MsSearch search,
            MsRunSearch runSearch, XMLStreamWriter writer, String basefile)
            throws XMLStreamException {
        // search summary
        writer.writeStartElement("search_summary");
        writer.writeAttribute("base_name", basefile);
        writer.writeAttribute("search_engine", search.getSearchProgram().toString());
        
        writer.writeAttribute("precursor_mass_type", "monoisotopic");
        writer.writeAttribute("fragment_mass_type", "average");
        writer.writeAttribute("search_id", String.valueOf(runSearch.getId()));
        newLine(writer);
        
        // search database
        writer.writeStartElement("search_database");
        List<MsSearchDatabase> dbs = search.getSearchDatabases();
        writer.writeAttribute("local_path", dbs.get(0).getDatabaseFileName());
        writer.writeAttribute("type", "AA");
        writer.writeEndElement();
        newLine(writer);
        
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
    }
    
    abstract MassType getPrecursorMassType(int searchId);
    
    abstract MassType getFragmentMassType(int searchId);
    
    
    
    void newLine(XMLStreamWriter writer ) throws XMLStreamException {
        writer.writeCharacters("\n");
    }
    
}
