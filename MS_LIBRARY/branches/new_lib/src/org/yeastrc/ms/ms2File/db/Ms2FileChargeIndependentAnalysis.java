/**
 * Ms2FileChargeIndependentAnalysis.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.db;

/**
 * Represents an "I" line in the MS2 file.  Charge independent analysis for a particular scan.
 * There can be multiple "I" lines in the MS2 file for a single scan.
 */
public class Ms2FileChargeIndependentAnalysis {

    private int id;             // unique id (database)
    private int scanId;         // id (database) of the scan that was analyzed
    private String header;      // the label of the analysis data
    private String value;       // the value of the analysis data
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
    /**
     * @param scanId the scanId to set
     */
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }
    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    
}
