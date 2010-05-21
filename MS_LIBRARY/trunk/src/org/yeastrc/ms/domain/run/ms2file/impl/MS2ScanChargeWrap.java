/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;


/**
 * Represents a "Z" line (and any following "D" lines) from a MS2 file.  Describes the charge for a scan.
 * A scan can have multiple predicted charges
 */
public class MS2ScanChargeWrap {

    
    private int scanId;     // the id (database) of the scan to which this charge corresponds
    private MS2ScanCharge scanCharge;
    
    public MS2ScanChargeWrap(MS2ScanCharge scanCharge, int scanId) {
        this.scanCharge = scanCharge;
        this.scanId = scanId;
    }
    
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
   
    /**
     * @return the charge
     */
    public int getCharge() {
        return scanCharge.getCharge();
    }
    
    /**
     * @return the mass
     */
    public BigDecimal getMass() {
        return scanCharge.getMass();
    }
}
