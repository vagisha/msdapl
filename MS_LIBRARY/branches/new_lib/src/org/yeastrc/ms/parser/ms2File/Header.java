/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Run;

/**
 * 
 */
public class Header implements MS2Run {

    // These are header items we know and care about
    public static final String DANALYZER_OPTIONS = "DAnalyzerOptions";
    public static final String DANALYZER_VERSION = "DAnalyzerVersion";
    public static final String DANALYZER = "DAnalyzer";
    public static final String IANALYZER_OPTIONS = "IAnalyzerOptions";
    public static final String IANALYZER_VERSION = "IAnalyzerVersion";
    public static final String IANALYZER = "IAnalyzer";
    public static final String INSTRUMENT_SN = "InstrumentSN";
    public static final String INSTRUMENT_TYPE = "InstrumentType";
    public static final String EXTRACTOR_OPTIONS = "ExtractorOptions";
    public static final String EXTRACTOR_VERSION = "ExtractorVersion";
    public static final String EXTRACTOR = "Extractor";
    public static final String CREATION_DATE = "CreationDate";
    public static final String COMMENTS = "Comments";
    public static final String ACQUISITION_METHOD = "AcquisitionMethod";
    public static final String DATA_TYPE = "DataType";
    
    private Map<String, String> headerItems;
    private List<MS2Field> headerList;
    private String fileName;
    private String sha1Sum;
    
    public Header() {
        headerItems = new HashMap<String, String>();
        headerList = new ArrayList<MS2Field>();
    }
    
    public void addHeaderItem(String label, String value) {
        if (label == null || value == null)   return;
        headerItems.put(label, value);
        headerList.add(new HeaderItem(label, value));
    }
    
    public Iterator<Entry<String,String>> iterator() {
        return headerItems.entrySet().iterator();
    }
    
    public int headerCount() {
        return headerItems.size();
    }
    
    public boolean isHeaderValid() {
        if (getHeaderValueForLabel(CREATION_DATE)        == null ||
            getHeaderValueForLabel(EXTRACTOR)            == null ||
            getHeaderValueForLabel(EXTRACTOR_VERSION)    == null ||
            getHeaderValueForLabel(EXTRACTOR_OPTIONS)    == null)
            return false;
        return true;
    }
    
    public String getHeaderValueForLabel(String label) {
        return headerItems.get(label);
    }
    
    public String getCreationDate() {
        return getHeaderValueForLabel(CREATION_DATE);
    }
    
    public String getInstrumentModel() {
        return getHeaderValueForLabel(INSTRUMENT_TYPE);
    }
    
    public String getInstrumentSN() {
        return getHeaderValueForLabel(INSTRUMENT_SN);
    }
    
    public String getConversionSW() {
        return getHeaderValueForLabel(EXTRACTOR);
    }
    
    public String getConversionSWVersion() {
        return getHeaderValueForLabel(EXTRACTOR_VERSION);
    }
    
    public String getConversionSWOptions() {
        return getHeaderValueForLabel(EXTRACTOR_OPTIONS);
    }
    
    public String getAcquisitionMethod() {
        return getHeaderValueForLabel(ACQUISITION_METHOD);
    }
    
    public String getDataType() {
        return getHeaderValueForLabel(DATA_TYPE);
    }
    
    public String getComment() {
        String comments = getHeaderValueForLabel(COMMENTS);
        if (comments == null)
            // older versions may have Comment instead of Comments
            comments = getHeaderValueForLabel("Comment");
        return comments;
    }
    
    
    public String toString() {
        Map<String, String> sortedItems = new TreeMap<String, String>(headerItems);
        
        StringBuilder buf = new StringBuilder();
        for (String headerItem: sortedItems.keySet()) {
            if (headerItem == null)
                continue;
            buf.append("H\t");
            buf.append(headerItem);
            buf.append("\t");
            buf.append(headerItems.get(headerItem));
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove the last new line character.
        return buf.toString();
    }

    public List<MS2Field> getHeaderList() {
        return headerList;
    }

    public List<MsEnzyme> getEnzymeList() {
        return new ArrayList<MsEnzyme>(0);
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getInstrumentVendor() {
        return null;
    }

    public RunFileFormat getRunFileFormat() {
        return RunFileFormat.MS2;
    }

    public String getSha1Sum() {
        return this.sha1Sum;
    }
    
    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }
}
