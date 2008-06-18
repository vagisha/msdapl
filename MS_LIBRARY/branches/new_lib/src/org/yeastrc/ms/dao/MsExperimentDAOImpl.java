/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsExperiment;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsExperimentDAT#load(int)
     */
    public MsExperiment load(int msExperimentId) {
        return (MsExperiment)queryForObject("MsExperiment.select", msExperimentId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsExperimentDAT#save(org.yeastrc.ms.MsExperiment)
     */
    public int save(MsExperiment experiment) {
        return (Integer) insert("MsExperiment.insert", experiment);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsExperimentDAT#update(org.yeastrc.ms.MsExperiment)
     */
    public void update(MsExperiment experiment) {
        
    }
}
