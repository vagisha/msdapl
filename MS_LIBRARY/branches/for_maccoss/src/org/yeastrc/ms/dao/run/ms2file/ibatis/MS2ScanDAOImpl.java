/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ScanDAOImpl extends BaseSqlMapDAO implements MS2ScanDAO {

    private MsScanDAO msScanDao;
    private MS2ChargeIndependentAnalysisDAO iAnalDao;
    private MS2ScanChargeDAO chargeDao;
    
    public MS2ScanDAOImpl(SqlMapClient sqlMap, MsScanDAO msScanDAO,
            MS2ChargeIndependentAnalysisDAO iAnalDao, MS2ScanChargeDAO chargeDao) {
        super(sqlMap);
        this.msScanDao = msScanDAO;
        this.iAnalDao = iAnalDao;
        this.chargeDao = chargeDao;
    }

    /**
     * This will return a MS2FileScan object. NO check is made to determine if 
     * the run this scan belongs to is of type MS2
     */
    public MS2Scan load(int scanId) {
        return (MS2Scan) queryForObject("MS2Scan.select", scanId);
    }
    
    /**
     * Returns a list of scan ids for the given run.
     */
    public List<Integer> loadScanIdsForRun(int runId) {
        
        // TODO: should we check if the run for the given id is a MS2 run?
        return msScanDao.loadScanIdsForRun(runId);
    }
    
    @Override
    public int numScans(int runId) {
        return msScanDao.numScans(runId);
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
