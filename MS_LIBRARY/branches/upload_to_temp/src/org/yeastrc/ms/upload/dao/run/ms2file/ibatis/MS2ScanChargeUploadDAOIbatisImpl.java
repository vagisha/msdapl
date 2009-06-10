/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ScanChargeWrap;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeDependentAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2ScanChargeUploadDAOIbatisImpl extends BaseSqlMapDAO implements MS2ScanChargeUploadDAO {

    private MS2ChargeDependentAnalysisUploadDAO dAnalysisDao;
    
    public MS2ScanChargeUploadDAOIbatisImpl(SqlMapClient sqlMap, MS2ChargeDependentAnalysisUploadDAO dAnalysisDao) {
        super(sqlMap);
        this.dAnalysisDao = dAnalysisDao;
    }

    public int save(MS2ScanCharge scanCharge, int scanId) {
        
        int id = saveScanChargeOnly(scanCharge, scanId);
        
        // save any charge dependent anaysis with the scan charge object
        for (MS2NameValuePair dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisDao.save(dAnalysis, id);
        }
        return id;
    }

    public int saveScanChargeOnly(MS2ScanCharge scanCharge, int scanId) {
        MS2ScanChargeWrap scanChargeDb = new MS2ScanChargeWrap(scanCharge, scanId);
        return saveAndReturnId("MS2ScanCharge.insert", scanChargeDb);
    }

    @Override
    public List<MS2ScanCharge> loadScanChargesForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectForScan", scanId);
    }
}
