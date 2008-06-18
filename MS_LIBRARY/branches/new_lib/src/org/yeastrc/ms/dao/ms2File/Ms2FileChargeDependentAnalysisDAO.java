/**
 * Ms2FileChargeDependentAnalysisDAO.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.Ms2FileChargeDependentAnalysis;

/**
 * 
 */
public interface Ms2FileChargeDependentAnalysisDAO {

    public abstract void save(Ms2FileChargeDependentAnalysis analysis);

    public abstract List<Ms2FileChargeDependentAnalysis> loadAnalysisForScanCharge(int scanChargeId);
}
