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
import org.yeastrc.ms.dao.ms2File.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileChargeDependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeDependentAnalysisDAO {

    public MS2FileChargeDependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2ChargeDependentAnalysisDb> loadAnalysisForScanCharge(int scanChargeId) {
        return queryForList("MS2ChgDAnalysis.selectAnalysisForCharge", scanChargeId);
    }

    public void save(MS2Field analysis, int scanChargeId) {
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
