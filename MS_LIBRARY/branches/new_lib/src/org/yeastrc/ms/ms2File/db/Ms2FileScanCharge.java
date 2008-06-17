/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.MsScanCharge;

/**
 * Represents a "Z" line from a MS2 file.  Describes the charge for a scan.
 * A scan can have multiple predicted charges
 */
public class Ms2FileScanCharge extends MsScanCharge {

    private List<Ms2FileChargeDependentAnalysis> chargeDepAnalysis;
    
    public Ms2FileScanCharge() {
        chargeDepAnalysis = new ArrayList<Ms2FileChargeDependentAnalysis>();
    }

    /**
     * @return the chargeDepAnalysis
     */
    public List<Ms2FileChargeDependentAnalysis> getChargeDepAnalysis() {
        return chargeDepAnalysis;
    }

    /**
     * @param chargeDepAnalysis the chargeDepAnalysis to set
     */
    public void setChargeDepAnalysis(
            List<Ms2FileChargeDependentAnalysis> chargeDepAnalysis) {
        this.chargeDepAnalysis = chargeDepAnalysis;
    }
    
    /**
     * @return the number of charge dependent analyses for this predicted charge for a scan.
     */
    public int getChargeDependentAnalysisCount() {
        return chargeDepAnalysis.size();
    }

    /**
     * @return {@link Iterator} for the list of {@link Ms2FileChargeDependentAnalysis} for this
     * predicted scan.
     */
    public Iterator<Ms2FileChargeDependentAnalysis> getAnalysisIterator() {
        return chargeDepAnalysis.iterator();
    }
}
