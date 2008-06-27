/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeIndependentAnalysis;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileChargeIndependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2FileChargeIndependentAnalysisDAO {

    public MS2FileChargeIndependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2FileChargeIndependentAnalysis> loadAnalysisForScan(int scanId) {
        return queryForList("Ms2FileChargeIndependentAnalysis.selectAnalysisForCharge", scanId);
    }

    public boolean save(MS2FileChargeIndependentAnalysis analysis) {
        return save("Ms2FileChargeIndependentAnalysis.insert", analysis);
    }

}
