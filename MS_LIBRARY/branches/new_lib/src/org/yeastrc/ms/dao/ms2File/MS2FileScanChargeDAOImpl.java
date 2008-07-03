/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2FileScanChargeDAOImpl extends BaseSqlMapDAO implements MS2FileScanChargeDAO {

    public MS2FileScanChargeDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MS2FileScanCharge scanCharge) {
        int id = saveAndReturnId("MS2ScanCharge.insert", scanCharge);
        
        // save any charge dependent anaysis with the scan charge object
        MS2FileChargeDependentAnalysisDAO dAnalysisDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
        for (MS2FileChargeDependentAnalysis dAnalysis: scanCharge.getChargeDepAnalysis()) {
            dAnalysisDao.save(dAnalysis);
        }
        return id;
    }
    
    public MS2FileScanCharge load(int scanChargeId) {
        return (MS2FileScanCharge) queryForObject("MS2ScanCharge.select", scanChargeId);
    }
    
    public List<MS2FileScanCharge> loadChargesForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectChargesForScan", scanId);
    }

    public void deleteByRunId(int runId) {
        delete("MS2ScanCharge.deleteByRunId", runId);
    }
}
