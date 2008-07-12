/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2HeaderDAO;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2HeaderDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileHeaderDAOImpl extends BaseSqlMapDAO implements MS2HeaderDAO {

    public MS2FileHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2Field header, int runId) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("runId", runId);
        map.put("name", header.getName());
        map.put("value", header.getValue());
        save("MS2Header.insert", map);
    }
    
    public List<MS2HeaderDb> loadHeadersForRun(int runId) {
        return queryForList("MS2Header.selectHeadersForRun", runId);
    }

    public void deleteHeadersForRunId(int runId) {
        delete("MS2Header.deleteByRunId", runId);
    }

    public void deleteHeadersForRunIds(List<Integer> runIds) {
        if (runIds == null || runIds.size() == 0)   return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("runIdList", runIds);
        delete("MS2Header.deleteByRunIds", map);
    }
}
