/**
 * MS2ScanBase.java
 * @author Vagisha Sharma
 * Jul 12, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;

/**
 * 
 */
public interface MS2ScanBase {

    /**
     * @return the scanChargeList
     */
    public abstract List<? extends MS2ScanCharge> getScanChargeList();

    /**
     * @return list of charge independent analysis for the scan.
     */
    public abstract List<? extends MS2Field> getChargeIndependentAnalysisList();
}
