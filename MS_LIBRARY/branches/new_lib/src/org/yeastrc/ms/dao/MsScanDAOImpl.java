/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

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
}
