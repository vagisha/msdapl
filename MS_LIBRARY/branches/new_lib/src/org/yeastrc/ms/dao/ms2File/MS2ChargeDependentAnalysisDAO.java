/**
 * Ms2FileChargeDependentAnalysisDAO.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

/**
 * 
 */
public interface MS2ChargeDependentAnalysisDAO {

    public abstract void save(MS2Field analysis, int scanChargeId);

    public abstract List<MS2ChargeDependentAnalysisDb> loadAnalysisForScanCharge(int scanChargeId);
    
    public abstract void deleteByScanChargeId(int scanChargeId);
}
