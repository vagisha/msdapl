/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * 
 */
public class Header {

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
    
    private HashMap<String, String> headerItems;
    
    public Header() {
        headerItems = new HashMap<String, String>();
    }
    
    public void addHeaderItem(String label, String value) {
        if (label == null || value == null)   return;
        headerItems.put(label, value);
    }
    
    public Iterator<Entry<String,String>> iterator() {
        return headerItems.entrySet().iterator();
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
    
    public String getComments() {
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
}
