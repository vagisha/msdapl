/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.Ms2FileChargeDependentAnalysis;

/**
 * 
 */
public class Ms2FileChargeDependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements Ms2FileChargeDependentAnalysisDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAO#loadAnalysisForScanCharge(int)
     */
    public List<Ms2FileChargeDependentAnalysis> loadAnalysisForScanCharge(int scanChargeId) {
        return queryForList("Ms2FileChargeDependentAnalysis.selectAnalysisForCharge", scanChargeId);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAO#save(org.yeastrc.ms.ms2File.Ms2FileChargeDependentAnalysis)
     */
    public void save(Ms2FileChargeDependentAnalysis analysis) {
        insert("Ms2FileChargeDependentAnalysis.insert", analysis);
    }

}
