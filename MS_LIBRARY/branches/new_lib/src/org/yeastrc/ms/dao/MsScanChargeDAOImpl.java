/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsScanCharge;

/**
 * 
 */
public class MsScanChargeDAOImpl extends BaseSqlMapDAO implements MsScanChargeDAO {

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsScanChargeDAO#save(org.yeastrc.ms.MsScanCharge)
     */
    public int save(MsScanCharge scanCharge) {
        return (Integer)insert("MsScanCharge.insert", scanCharge);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsScanChargeDAO#load(int)
     */
    public MsScanCharge load(int scanChargeId) {
        return (MsScanCharge) queryForObject("MsScanCharege.select", scanChargeId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsScanChargeDAO#update(org.yeastrc.ms.MsScanCharge)
     */
    public void update(MsScanCharge run) {
        
    }

    @Override
    public List<MsScanCharge> loadChargesForScan(int scanId) {
        return queryForList("MsScanCharge.selectChargesForScan", scanId);
    }
    
}
