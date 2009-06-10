/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeIndependentAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ScanUploadDAOIbatisImpl extends BaseSqlMapDAO implements MS2ScanUploadDAO {

    private MsScanUploadDAO msScanDao;
    private MS2ChargeIndependentAnalysisUploadDAO iAnalDao;
    private MS2ScanChargeUploadDAO chargeDao;
    
    public MS2ScanUploadDAOIbatisImpl(SqlMapClient sqlMap, MsScanUploadDAO msScanDAO,
            MS2ChargeIndependentAnalysisUploadDAO iAnalDao, MS2ScanChargeUploadDAO chargeDao) {
        super(sqlMap);
        this.msScanDao = msScanDAO;
        this.iAnalDao = iAnalDao;
        this.chargeDao = chargeDao;
    }

    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        return msScanDao.loadScanIdForScanNumRun(scanNum, runId);
    }
    
    /**
     * Saves the scan along with any MS2 file format specific data.
     */
    public int save(MS2ScanIn scan, int runId, int precursorScanId) {
        
        // save the parent scan first
        int scanId = msScanDao.save(scan, runId, precursorScanId);
        
        // save the charge independent analysis
        for (MS2NameValuePair iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalDao.save(iAnalysis, scanId);
        }
        
        // save the charge state
        for (MS2ScanCharge charge: scan.getScanChargeList()) {
            chargeDao.save(charge, scanId);
        }
        
        return scanId;
    }
    
    @Override
    public <T extends MsScanIn> List<Integer> save(List<T> scans, int runId) {
        return msScanDao.save(scans, runId);
    }
    
    public int save(MS2ScanIn scan, int runId) {
        return save(scan, runId, 0);
    }
    
    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        msScanDao.delete(scanId);
    }
}
