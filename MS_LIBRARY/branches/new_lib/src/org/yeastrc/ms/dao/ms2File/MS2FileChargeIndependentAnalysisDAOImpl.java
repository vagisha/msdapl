/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return queryForList("MS2ChgIAnalysis.selectAnalysisForCharge", scanId);
    }

    public void save(MS2FileChargeIndependentAnalysis analysis) {
        save("MS2ChgIAnalysis.insert", analysis);
    }

    public void deleteByScanId(int scanId) {
        delete("MS2ChgIAnalysis.deleteByScanId", scanId);
    }

    public void deleteByScanIds(List<Integer> scanIds) {
        if (scanIds == null || scanIds.size() == 0)
            return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("scanIdList", scanIds);
        delete("MS2ChgIAnalysis.deleteByScanIds", map);
    }

    public void deleteByRunId(int runId) {
        delete("MS2ChgIAnalysis.deleteByRunId", runId);
    }

}
