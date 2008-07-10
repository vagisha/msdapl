/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeDependentAnalysis;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileChargeDependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2FileChargeDependentAnalysisDAO {

    public MS2FileChargeDependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2FileChargeDependentAnalysis> loadAnalysisForScanCharge(int scanChargeId) {
        return queryForList("MS2ChgDAnalysis.selectAnalysisForCharge", scanChargeId);
    }

    public void save(MS2FileChargeDependentAnalysis analysis) {
        save("MS2ChgDAnalysis.insert", analysis);
    }

    public void deleteByScanChargeId(int scanChargeId) {
        delete("MS2ChgDAnalysis.deleteByScanChargeId", scanChargeId);
    }

}
