/**
 * MS2ScanDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.MS2ScanBase;
import org.yeastrc.ms.domain.MsScanDb;

/**
 * 
 */
public interface MS2ScanDb extends MS2ScanBase, MsScanDb {

    public abstract List<MS2ScanChargeDb> getScanChargeList();
    
    public abstract List<MS2ChargeIndependentAnalysisDb> getChargeIndependentAnalysisList();
}
