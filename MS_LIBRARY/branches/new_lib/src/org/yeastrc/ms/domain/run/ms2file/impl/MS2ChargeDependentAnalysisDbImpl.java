/**
 * MS2ChargeDependentAnalysisDbImpl.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisDb;


/**
 * Represents a "D" line in the MS2 file.  Charge dependent analysis for a particular scan.
 * A "D" line should follow a "Z" line in the MS2 file.
 */
public class MS2ChargeDependentAnalysisDbImpl extends BaseHeader implements MS2ChargeDependentAnalysisDb {

    private int id;                         // unique id (used for database)
    private int scanChargeId;               // the predicated charge for a particular scan to which
                                            // this charge dependent analysis corresponds.
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
}
