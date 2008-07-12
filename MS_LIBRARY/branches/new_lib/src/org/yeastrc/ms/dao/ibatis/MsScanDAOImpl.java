/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO<MsScan, MsScanDb> {

    public MsScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MsScan scan, int runId, int precursorScanId) {
        MsScanSqlMapParam scanDb = new MsScanSqlMapParam(runId, precursorScanId, scan);
        return saveAndReturnId("MsScan.insert", scanDb);
    }
    
    public MsScanDb load(int scanId) {
        return (MsScanDb) queryForObject("MsScan.select", scanId);
    }
    
    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }

    public void delete(int scanId) {
        delete("MsScan.delete", scanId);
    }
    
    public void deleteScansForRun(int runId) {
        delete("MsScan.deleteByRunId", runId);
    }

}
