/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.domain.ms2File.db.MS2FileChargeDependentAnalysis;
import org.yeastrc.ms.domain.ms2File.db.MS2FileScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2FileScanChargeDAOImpl extends BaseSqlMapDAO implements MS2FileScanChargeDAO {

    public MS2FileScanChargeDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<Integer> loadScanChargeIdsForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectIdsForScan", scanId);
    }
    
    public List<MS2FileScanCharge> loadScanChargesForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectForScan", scanId);
    }
    
    public List<MS2FileScanCharge> loadScanChargesForScan(int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MS2ScanCharge.selectForScanAndCharge", map);
    }
    
    public int save(MS2FileScanCharge scanCharge) {
        
        int id = saveAndReturnId("MS2ScanCharge.insert", scanCharge);
        
        // save any charge dependent anaysis with the scan charge object
        MS2FileChargeDependentAnalysisDAO dAnalysisDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
        for (MS2FileChargeDependentAnalysis dAnalysis: scanCharge.getChargeDependentAnalysis()) {
            dAnalysis.setScanChargeId(id);
            dAnalysisDao.save(dAnalysis);
        }
        return id;
    }
    

    public void deleteByScanId(int scanId) {
        
        // get a list of scan charge ids associated with the scanId
        // delete all charge dependent analyses
        List<Integer> scanChargeIds = loadScanChargeIdsForScan(scanId);
        for (Integer id: scanChargeIds) {
            MS2FileChargeDependentAnalysisDAO dAnalysisDao = DAOFactory.instance().getMs2FileChargeDAnalysisDAO();
            dAnalysisDao.deleteByScanChargeId(id);
        }
       
        // delete the scan charge entries for the scanId
        delete("MS2ScanCharge.deleteByScanId", scanId);
    }

    public void deleteByScanIdCascade(int scanId) {
        delete("MS2ScanCharge.deleteByScanId_cascade", scanId);
    }
   
}
