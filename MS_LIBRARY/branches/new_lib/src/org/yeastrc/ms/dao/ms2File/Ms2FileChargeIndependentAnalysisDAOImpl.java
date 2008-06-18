/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.Ms2FileChargeIndependentAnalysis;

/**
 * 
 */
public class Ms2FileChargeIndependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements Ms2FileChargeIndependentAnalysisDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAO#loadAnalysisForScan(int)
     */
    public List<Ms2FileChargeIndependentAnalysis> loadAnalysisForScan(int scanId) {
        return queryForList("Ms2FileChargeIndependentAnalysis.selectAnalysisForCharge", scanId);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAO#save(org.yeastrc.ms.ms2File.Ms2FileChargeIndependentAnalysis)
     */
    @Override
    public void save(Ms2FileChargeIndependentAnalysis analysis) {
        insert("Ms2FileChargeIndependentAnalysis.insert", analysis);
    }

}
