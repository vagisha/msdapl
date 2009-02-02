/**
 * SequestPepXmlConverter.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.pepxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;

/**
 * 
 */
public class SequestPepXmlConverter extends PepXmlConverter {

    private static final DAOFactory daofactory = DAOFactory.instance();
    private static final SequestSearchDAO seqSearchDao = daofactory.getSequestSearchDAO();
    private static final SQTRunSearchDAO runSearchDao = daofactory.getSqtRunSearchDAO();
    private static final MsRunDAO runDao = daofactory.getMsRunDAO();
    private static final MsScanDAO scanDao = daofactory.getMsScanDAO();
    private static final SQTSearchScanDAO sqtScanDao = daofactory.getSqtSpectrumDAO();
    private static final SequestSearchResultDAO resultDao = daofactory.getSequestResultDAO();
    
    private static final Logger log = Logger.getLogger(MsDataToPepXmlConverter.class);
    
    private List<MsResidueModification> staticMods;
    
    public void convertSearch(int searchId, String outfile) {
        
    }
    
    public void convertRunSearcn(int runSearchId, String outdir) {
        SQTRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("RunSearch with ID "+runSearchId+" not found!");
            throw new IllegalArgumentException("RunSearch with ID "+runSearchId+" not found!");
        }
        
        SequestSearch search = seqSearchDao.loadSearch(runSearch.getSearchId());
        this.staticMods = search.getStaticResidueMods();
        
        String outfile = runSearchDao.loadFilenameForRunSearch(runSearchId)+".pep.xml";
        outfile = outdir+File.separator+outfile;
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            startMsmsRunSummary(runSearchId, writer);
            writeEnzymes(search, writer);
            String basefile = runSearchDao.loadFilenameForRunSearch(runSearch.getId());
            // write search summary; "search_summary" element
            writeSearchSummary(search, runSearch, writer, basefile);
            writeSearchResults(runSearch, writer, basefile);
            endMsmsRunSummary(writer);
            endMsmsPipelineAnalysis(writer);
        }
        catch (FileNotFoundException e) {
            log.error("", e);
        }
        catch (XMLStreamException e) {
            log.error("",e);
        }
        catch (DatatypeConfigurationException e) {
            log.error("",e);
        }
        finally {
            if(writer != null) try {
                writer.close();
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    private String getPrecursorMassType(MsSearch search) {
        
    }
    
}
