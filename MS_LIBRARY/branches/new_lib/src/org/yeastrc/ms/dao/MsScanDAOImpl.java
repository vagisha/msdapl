/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsScan;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsScanDAO#save(org.yeastrc.ms.MsScan)
     */
    public int save(MsScan run) {
        return (Integer)insert("MsScan.insert", run);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsScanDAO#load(int)
     */
    public MsScan load(int runId) {
        return (MsScan) queryForObject("MsScan.select", runId);
    }

    @Override
    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }
}
