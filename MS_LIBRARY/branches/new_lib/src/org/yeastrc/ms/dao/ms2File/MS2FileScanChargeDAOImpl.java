/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dao.MS2FileScanChargeDAO;
import org.yeastrc.ms.dto.MsScanCharge;

public class MS2FileScanChargeDAOImpl extends BaseSqlMapDAO implements MS2FileScanChargeDAO {

    public int save(MsScanCharge scanCharge) {
        return (Integer)insert("scanCharge.insert", scanCharge);
    }
    
    public MsScanCharge load(int scanChargeId) {
        return (MsScanCharge) queryForObject("scanCharge.select", scanChargeId);
    }
    
    public List<MsScanCharge> loadChargesForScan(int scanId) {
        return queryForList("scanCharge.selectChargesForScan", scanId);
    }
    
}
