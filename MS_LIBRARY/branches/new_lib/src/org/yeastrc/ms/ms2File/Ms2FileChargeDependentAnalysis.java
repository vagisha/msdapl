/**
 * Ms2FileChargeDependentAnalysis.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File;

/**
 * Represents a "D" line in the MS2 file.  Charge dependent analysis for a particular scan.
 * A "D" line should follow a "Z" line in the MS2 file.
 */
public class Ms2FileChargeDependentAnalysis {

    private int id;                         // unique id (used for database)
    private int scanChargeId;               // the predicated charge for a particular scan to which
                                            // this charge dependent analysis corresponds.
    private String header;                  // the label for the analysis
    private String value;                   // the value for the analysis
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
     * @return the scanChargeId
     */
    public int getScanChargeId() {
        return scanChargeId;
    }
    /**
     * @param scanChargeId the scanChargeId to set
     */
    public void setScanChargeId(int scanChargeId) {
        this.scanChargeId = scanChargeId;
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
