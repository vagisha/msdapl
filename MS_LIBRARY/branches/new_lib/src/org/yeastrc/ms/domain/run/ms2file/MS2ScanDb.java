/**
 * MS2ScanDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.MsScanDb;

/**
 * 
 */
public interface MS2ScanDb extends MsScanDb {

    public abstract List<MS2ScanChargeDb> getScanChargeList();
    
    public abstract List<MS2ChargeIndependentAnalysisDb> getChargeIndependentAnalysisList();
}
