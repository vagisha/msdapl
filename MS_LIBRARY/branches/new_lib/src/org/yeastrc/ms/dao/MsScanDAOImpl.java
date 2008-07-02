/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dto.MsScan;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO {

    public MsScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MsScan scan) {
        return saveAndReturnId("MsScan.insert", scan);
    }
    
    public MsScan load(int scanId) {
        return (MsScan) queryForObject("MsScan.select", scanId);
    }

    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }

    public void deleteScansForRun(int runId) {
        delete("MsScan.deleteByRunId", runId);
    }

    @Override
    public void deleteScansForRuns(List<Integer> runIds) {
        if (runIds == null || runIds.size() == 0)   return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("runIdList", runIds);
        delete("MsScan.deleteByRunIds", map);
    }
}
