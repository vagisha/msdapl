/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2FileScanChargeDAOImpl extends BaseSqlMapDAO implements MS2FileScanChargeDAO {

    public MS2FileScanChargeDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MS2FileScanCharge scanCharge) {
        return saveAndReturnId("scanCharge.insert", scanCharge);
    }
    
    public MS2FileScanCharge load(int scanChargeId) {
        return (MS2FileScanCharge) queryForObject("scanCharge.select", scanChargeId);
    }
    
    public List<MS2FileScanCharge> loadChargesForScan(int scanId) {
        return queryForList("scanCharge.selectChargesForScan", scanId);
    }
}
