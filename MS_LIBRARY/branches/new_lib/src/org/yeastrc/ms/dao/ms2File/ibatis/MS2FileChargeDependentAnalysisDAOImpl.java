/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.ms2File.db.MS2FileChargeDependentAnalysis;

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

    public void save(IHeader analysis, int scanChargeId) {
        Map<String, Object>map = new HashMap<String, Object>(3);
        map.put("scanId", scanChargeId);
        map.put("name", analysis.getName());
        map.put("value", analysis.getValue());
        save("MS2ChgDAnalysis.insert", map);
    }

    public void deleteByScanChargeId(int scanChargeId) {
        delete("MS2ChgDAnalysis.deleteByScanChargeId", scanChargeId);
    }

}
