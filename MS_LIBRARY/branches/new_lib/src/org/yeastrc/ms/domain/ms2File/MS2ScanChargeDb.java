/**
 * MS2ScanChargeDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.List;

/**
 * 
 */
public interface MS2ScanChargeDb extends MS2ScanCharge {

    /**
     * @return database id of the scan this belongs to
     */
    public abstract int getScanId();
    
    /**
     * @return database id of this scan + charge
     */
    public abstract int getId();
    
    /**
     * @return the list of charge dependent analyses for this scan + charge
     */
    public abstract List<MS2ChargeDependentAnalysisDb> getChargeDependentAnalysisList();

}
