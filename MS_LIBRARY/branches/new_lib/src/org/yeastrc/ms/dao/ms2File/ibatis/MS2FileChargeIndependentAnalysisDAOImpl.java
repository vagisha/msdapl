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
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.ms2File.db.MS2FileChargeIndependentAnalysis;

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
        return queryForList("MS2ChgIAnalysis.selectAnalysisForScan", scanId);
    }

    public void save(IHeader analysis, int scanId) {
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
