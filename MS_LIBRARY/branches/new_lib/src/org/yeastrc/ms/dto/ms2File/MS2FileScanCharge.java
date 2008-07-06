/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a "Z" line (and any following "D" lines) from a MS2 file.  Describes the charge for a scan.
 * A scan can have multiple predicted charges
 */
public class MS2FileScanCharge {

    
    private int id;         // unique id (database)
    private int scanId;     // the id (database) of the scan to which this charge corresponds
    private int charge;     // the charge state
    private BigDecimal mass;     // predicted [M+H]+ (mass)
    
    private List<MS2FileChargeDependentAnalysis> chargeDepAnalysis;
 
    public MS2FileScanCharge() {
        chargeDepAnalysis = new ArrayList<MS2FileChargeDependentAnalysis>();
    }
    
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
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }
    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    /**
     * @return the mass
     */
    public BigDecimal getMass() {
        return mass;
    }
    /**
     * @param mass the mass to set
     */
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }

    /**
     * @return the chargeDepAnalysis
     */
    public List<MS2FileChargeDependentAnalysis> getChargeDependentAnalysis() {
        return chargeDepAnalysis;
    }

    public void addChargeDependentAnalysis(MS2FileChargeDependentAnalysis analysis) {
        chargeDepAnalysis.add(analysis);
    }
    
    /**
     * @param chargeDepAnalysis the chargeDepAnalysis to set
     */
    public void setChargeDependentAnalysis(
            List<MS2FileChargeDependentAnalysis> chargeDepAnalysis) {
        this.chargeDepAnalysis = chargeDepAnalysis;
    }
    
    /**
     * @return the number of charge dependent analyses for this predicted charge for a scan.
     */
    public int getChargeDependentAnalysisCount() {
        return chargeDepAnalysis.size();
    }
}
