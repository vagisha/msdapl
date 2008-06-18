/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 */
public class Ms2FileHeader {

    private static final String ACTIVATION_TYPE = "ActivationType";
    private static final String DANALYZER_OPTIONS = "DAnalyzerOptions";
    private static final String DANALYZER_VERSION = "DAnalyzerVersion";
    private static final String DANALYZER = "DAnalyzer";
    private static final String IANALYZER_OPTIONS = "IAnalyzerOptions";
    private static final String IANALYZER_VERSION = "IAnalyzerVersion";
    private static final String IANALYZER = "IAnalyzer";
    private static final String INSTRUMENT_SN = "InstrumentSN";
    private static final String INSTRUMENT_TYPE = "InstrumentType";
    private static final String EXTRACTOR_OPTIONS = "ExtractorOptions";
    private static final String EXTRACTOR_VERSION = "ExtractorVersion";
    private static final String EXTRACTOR = "Extractor";
    private static final String CREATION_DATE = "CreationDate";
    
    private HashMap<String, String> headerItems;
    
    public Ms2FileHeader() {
        headerItems = new HashMap<String, String>();
    }
    
    public void addHeaderItem(String label, String value) {
        if (label == null || value == null)   return;
        headerItems.put(label, value);
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
    
    public String getInstrumentType() {
        return getHeaderValueForLabel(INSTRUMENT_TYPE);
    }
    
    public String getInstrumentSN() {
        return getHeaderValueForLabel(INSTRUMENT_SN);
    }
    
    public String getExtractor() {
        return getHeaderValueForLabel(EXTRACTOR);
    }
    
    public String getExtractorVersion() {
        return getHeaderValueForLabel(EXTRACTOR_VERSION);
    }
    
    public String getExtractorOptions() {
        return getHeaderValueForLabel(EXTRACTOR_OPTIONS);
    }
    
    public String getIAnalyzer() {
        return getHeaderValueForLabel(IANALYZER);
    }
    
    public String getIAnalyzerVersion() {
        return getHeaderValueForLabel(IANALYZER_VERSION);
    }
    
    public String getIAnalyzerOptions() {
        return getHeaderValueForLabel(IANALYZER_OPTIONS);
    }
    
    public String getDAnalyzer() {
        return getHeaderValueForLabel(DANALYZER);
    }
    
    public String getDAnalyzerVersion() {
        return getHeaderValueForLabel(DANALYZER_VERSION);
    }
    
    public String getDAnalyzerOptions() {
        return getHeaderValueForLabel(DANALYZER_OPTIONS);
    }
    
    public String getActivationType() {
        return getHeaderValueForLabel(ACTIVATION_TYPE);
    }
    
    public String getComments() {
        String comments = getHeaderValueForLabel("Comments");
        if (comments == null)
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
}
