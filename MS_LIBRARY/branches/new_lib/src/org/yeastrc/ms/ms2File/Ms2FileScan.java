/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.MsScan;

/**
 * 
 */
public class Ms2FileScan extends MsScan {

    private List<Ms2FileChargeIndependentAnalysis> chargeIndepAnalysis;
    
    public Ms2FileScan() {
        chargeIndepAnalysis = new ArrayList<Ms2FileChargeIndependentAnalysis>();
    }

    /**
     * @return the chargeIndepAnalysis
     */
    public List<Ms2FileChargeIndependentAnalysis> getChargeIndepAnalysis() {
        return chargeIndepAnalysis;
    }

    /**
     * @param chargeIndepAnalysis the chargeIndepAnalysis to set
     */
    public void setChargeIndepAnalysis(
            List<Ms2FileChargeIndependentAnalysis> chargeIndepAnalysis) {
        this.chargeIndepAnalysis = chargeIndepAnalysis;
    }
    
    
}
