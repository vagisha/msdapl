/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.IMsScan;
import org.yeastrc.ms.domain.db.MsScan;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO<IMsScan, MsScan> {

    public MsScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(IMsScan scan, int runId) {
        MsScanDb scanDb = new MsScanDb(runId, scan);
        return saveAndReturnId("MsScan.insert", scanDb);
    }
    
    public MsScan load(int scanId) {
        return (MsScan) queryForObject("MsScan.select", scanId);
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
