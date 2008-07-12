/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileChargeIndependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeIndependentAnalysisDAO {

    public MS2FileChargeIndependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2ChargeIndependentAnalysisDb> loadAnalysisForScan(int scanId) {
        return queryForList("MS2ChgIAnalysis.selectAnalysisForScan", scanId);
    }

    public void save(MS2Field analysis, int scanId) {
        Map<String, Object>map = new HashMap<String, Object>(3);
        map.put("scanId", scanId);
        map.put("name", analysis.getName());
        map.put("value", analysis.getValue());
        save("MS2ChgIAnalysis.insert", map);
    }

    public void deleteByScanId(int scanId) {
        delete("MS2ChgIAnalysis.deleteByScanId", scanId);
    }

}
