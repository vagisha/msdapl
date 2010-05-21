/**
 * MS2ChargeDependentAnalysisDbImpl.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;



/**
 * Represents a "D" line in the MS2 file.  Charge dependent analysis for a particular scan.
 * A "D" line should follow a "Z" line in the MS2 file.
 */
public class MS2ChargeDependentAnalysisWrap implements MS2ChargeDependentAnalysisWId {

    private int scanChargeId;               // the predicated charge for a particular scan to which
                                            // this charge dependent analysis corresponds.
    private MS2NameValuePair header;
    
    public MS2ChargeDependentAnalysisWrap (MS2NameValuePair header, int scanChargeId) {
        this.header = header;
        this.scanChargeId = scanChargeId;
    }
    
    public int getScanChargeId() {
        return scanChargeId;
    }

    @Override
    public String getName() {
        return header.getName();
    }

    @Override
    public String getValue() {
        return header.getValue();
    }
}
