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
public class MS2FileScan extends MsScan {

    
    private List<MS2FileChargeIndependentAnalysis> chargeIndepAnalysis;
    private List <MS2FileScanCharge> scanCharges; // charge states for this scan
    
    public MS2FileScan() {
        chargeIndepAnalysis = new ArrayList<MS2FileChargeIndependentAnalysis>();
        scanCharges = new ArrayList<MS2FileScanCharge>();
    }

    /**
     * @return the chargeIndepAnalysis
     */
    public List<MS2FileChargeIndependentAnalysis> getChargeIndepAnalysisList() {
        return chargeIndepAnalysis;
    }

    /**
     * @param chargeIndepAnalysis the chargeIndepAnalysis to set
     */
    public void setChargeIndepAnalysisList(
            List<MS2FileChargeIndependentAnalysis> chargeIndepAnalysis) {
        this.chargeIndepAnalysis = chargeIndepAnalysis;
    }
    
    /**
     * @return the scanCharges
     */
    public List<MS2FileScanCharge> getScanChargeList() {
        return scanCharges;
    }

    /**
     * @param scanCharges the scanCharges to set
     */
    public void setScanChargeList(List<MS2FileScanCharge> scanCharges) {
        this.scanCharges = scanCharges;
    }
    
}
