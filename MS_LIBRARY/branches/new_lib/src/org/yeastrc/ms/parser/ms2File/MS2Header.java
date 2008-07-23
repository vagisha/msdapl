/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Run;

/**
 * 
 */
public class MS2Header implements MS2Run {

    private List<MS2Field> headerList;
    private String fileName;
    private String sha1Sum;
    private String creationDate;
    private String extractor;
    private String extractorVersion;
    private String extractorOptions;
    private String instrumentModel;
    private String instrumentSN;
    private String acquisionMethod;
    private String dataType;
    private StringBuilder comment;
    
    public MS2Header() {
        headerList = new ArrayList<MS2Field>();
        comment = new StringBuilder();
    }
    
    public void addHeaderItem(String name, String value) {
        
        if (name == null)
            throw new NullPointerException("name for Header cannot be null.");
        
        headerList.add(new HeaderItem(name, value));
        
        // if there is no value for this header ignore it; It will still get added to the 
        // headerItems list. 
        if (value == null || value.trim().length() == 0)
            return;
        
        if (isCreationDate(name))
            creationDate = value;
        if (isExtractor(name))
            extractor = value;
        if (isExtractorVersion(name)) 
            extractorVersion = value;
        if (isExtractorOptions(name))
            extractorOptions = value;
        if (isInstrumentModel(name))
            instrumentModel = value;
        if (isInstrumentSN(name))
            instrumentSN = value;
        if (isAcquisitionMethod(name))
            acquisionMethod = value;
        if (isDataType(name))
            dataType = value;
        if (isComment(name)) {
            comment.append(value+";");
        }
    }
    
    public int headerCount() {
        return headerList.size();
    }
    
    public boolean isValid() {
        if (creationDate        == null ||
            extractor           == null ||
            extractorVersion    == null ||
            extractorOptions    == null ||
            sha1Sum             == null ||
            fileName            == null)
            return false;
        return true;
    }
    
    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getSha1Sum() {
        return this.sha1Sum;
    }
    
    public String getFileName() {
        return fileName;
    }
   
    public String getCreationDate() {
        return creationDate;
    }
    
    public String getInstrumentModel() {
        return instrumentModel;
    }
    
    public String getInstrumentSN() {
        return instrumentSN;
    }
    
    public String getConversionSW() {
        return extractor;
    }
    
    public String getConversionSWVersion() {
        return extractorVersion;
    }
    
    public String getConversionSWOptions() {
        return extractorOptions;
    }
    
    public String getAcquisitionMethod() {
        return acquisionMethod;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public String getComment() {
        if (comment == null || comment.length() == 0)    return null;
        return comment.deleteCharAt(comment.length() -1).toString(); // delete last semi-colon
    }
    
    public List<MS2Field> getHeaderList() {
        return headerList;
    }

    // MS2 files don't have any enzyme information
    public List<MsEnzyme> getEnzymeList() {
        return new ArrayList<MsEnzyme>(0);
    }

    public String getInstrumentVendor() {
        return null;
    }

    public RunFileFormat getRunFileFormat() {
        return RunFileFormat.MS2;
    }

    public String toString() {
        
        StringBuilder buf = new StringBuilder();
        for (MS2Field headerItem: headerList) {
            if (headerItem == null)
                continue;
            buf.append("H\t");
            buf.append(headerItem.getName());
            buf.append("\t");
            buf.append(headerItem.getValue());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove the last new line character.
        return buf.toString();
    }
    
    private boolean isCreationDate(String value) {
        return value.equalsIgnoreCase("CreationDate");
    }
    
    private boolean isExtractor(String value) {
        return value.equalsIgnoreCase("Extractor");
    }
    
    private boolean isExtractorVersion(String value) {
        return value.equalsIgnoreCase("ExtractorVersion");
    }
    
    private boolean isExtractorOptions(String value) {
        return value.equalsIgnoreCase("ExtractorOptions");
    }
    
    private boolean isInstrumentModel(String value) {
        return value.equalsIgnoreCase("InstrumentType");
    }
    
    private boolean isInstrumentSN(String value) {
        return value.equalsIgnoreCase("InstrumentSN");
    }
    
    private boolean isAcquisitionMethod(String value) {
        return value.equalsIgnoreCase("AcquisitionMethod");
    }
    
    private boolean isDataType(String value) {
        return value.equalsIgnoreCase("DataType");
    }
    
    private boolean isComment(String value) {
        return value.startsWith("Comment");
    }
    
}
