/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dto.MsScan;

/**
 * 
 */
public class Ms2FileScan extends MsScan {

    
    private List<Ms2FileChargeIndependentAnalysis> chargeIndepAnalysis;
    private List <Ms2FileScanCharge> scanCharges; // charge states for this scan
    
    public Ms2FileScan() {
        chargeIndepAnalysis = new ArrayList<Ms2FileChargeIndependentAnalysis>();
        scanCharges = new ArrayList<Ms2FileScanCharge>();
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
    
    /**
     * @return the scanCharges
     */
    public List<Ms2FileScanCharge> getScanCharges() {
        return scanCharges;
    }

    /**
     * @param scanCharges the scanCharges to set
     */
    public void setScanCharges(List<Ms2FileScanCharge> scanCharges) {
        this.scanCharges = scanCharges;
    }

    
    
}
