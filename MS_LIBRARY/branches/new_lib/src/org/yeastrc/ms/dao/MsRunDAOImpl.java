/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsRun;

/**
 * 
 */
public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO {

    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsRunDAO#save(org.yeastrc.ms.MsRun)
     */
    public int save(MsRun run) {
        return (Integer)insert("MsRun.insert", run);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsRunDAO#load(int)
     */
    public MsRun load(int runId) {
        return (MsRun) queryForObject("MsRun.select", runId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsRunDAO#update(org.yeastrc.ms.MsRun)
     */
    public void update(MsRun run) {
        
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsRunDAO#loadRunsForExperiment(int)
     */
    public List<MsRun> loadRunsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
}
