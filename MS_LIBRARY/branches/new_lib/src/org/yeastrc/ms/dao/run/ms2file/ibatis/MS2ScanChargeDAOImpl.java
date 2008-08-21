/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanChargeDb;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2ScanChargeDAOImpl extends BaseSqlMapDAO implements MS2ScanChargeDAO {

    private MS2ChargeDependentAnalysisDAO dAnalysisDao;
    
    public MS2ScanChargeDAOImpl(SqlMapClient sqlMap, MS2ChargeDependentAnalysisDAO dAnalysisDao) {
        super(sqlMap);
        this.dAnalysisDao = dAnalysisDao;
    }

    public List<Integer> loadScanChargeIdsForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectIdsForScan", scanId);
    }
    
    public List<MS2ScanChargeDb> loadScanChargesForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectForScan", scanId);
    }
    
    public List<MS2ScanChargeDb> loadScanChargesForScanAndCharge(int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MS2ScanCharge.selectForScanAndCharge", map);
    }
    
    public int save(MS2ScanCharge scanCharge, int scanId) {
        
        int id = saveScanChargeOnly(scanCharge, scanId);
        
        // save any charge dependent anaysis with the scan charge object
        for (MS2Field dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisDao.save(dAnalysis, id);
        }
        return id;
    }

    public int saveScanChargeOnly(MS2ScanCharge scanCharge, int scanId) {
        MS2ScanChargeSqlMapParam scanChargeDb = new MS2ScanChargeSqlMapParam(scanId, scanCharge.getCharge(), scanCharge.getMass());
        int id = saveAndReturnId("MS2ScanCharge.insert", scanChargeDb);
        return id;
    }

    public void deleteByScanId(int scanId) {
        // delete the scan charge entries for the scanId
        delete("MS2ScanCharge.deleteByScanId", scanId);
    }
  
    public static final class MS2ScanChargeSqlMapParam implements MS2ScanChargeDb {
        private int scanID;
        private int charge;
        private BigDecimal mass;
        
        public MS2ScanChargeSqlMapParam(int scanID, int charge, BigDecimal mass) {
            this.scanID = scanID;
            this.charge = charge;
            this.mass = mass;
        }

        public int getScanId() {
            return scanID;
        }

        public int getCharge() {
            return charge;
        }

        public BigDecimal getMass() {
            return mass;
        }

        public List<MS2ChargeDependentAnalysisDb> getChargeDependentAnalysisList() {
            throw new UnsupportedOperationException("getChargeDependentAnalysisList() is not supported by MS2ScanChargeSqlMapParam");
        }

        public int getId() {
            throw new UnsupportedOperationException("getId() is not supported by MS2ScanChargeSqlMapParam");
        }
    }
}
