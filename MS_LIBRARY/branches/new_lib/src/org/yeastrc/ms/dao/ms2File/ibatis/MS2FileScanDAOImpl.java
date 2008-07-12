/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO<MS2Scan, MS2ScanDb> {

    private MsScanDAO<MsScan, MsScanDb> msScanDao;
    private MS2ChargeIndependentAnalysisDAO iAnalDao;
    private MS2ScanChargeDAO chargeDao;
    
    public MS2FileScanDAOImpl(SqlMapClient sqlMap, MsScanDAO<MsScan, MsScanDb> msScanDAO,
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
    public MS2ScanDb load(int scanId) {
        return (MS2ScanDb) queryForObject("MS2Scan.select", scanId);
    }
    
    /**
     * Returns a list of scan ids for the given run.
     */
    public List<Integer> loadScanIdsForRun(int runId) {
        
        // TODO: should we check if the run for the given id is a MS2 run?
        return msScanDao.loadScanIdsForRun(runId);
    }
    
    /**
     * Saves the scan along with any MS2 file format specific data.
     */
    public int save(MS2Scan scan, int runId, int precursorScanId) {
        
        // save the parent scan first
        int scanId = msScanDao.save(scan, runId, precursorScanId);
        
        // save the charge independent analysis
        for (MS2Field iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalDao.save(iAnalysis, scanId);
        }
        
        // save the charge dependent analysis
        for (MS2ScanCharge charge: scan.getScanChargeList()) {
            chargeDao.save(charge, scanId);
        }
        
        return scanId;
    }
    
    /**
     * Deletes all scans (and associated MS2 file format specific information) for 
     * the given run.  If the run is not of type MS2 nothing is deleted
     */
    public void deleteScansForRun(int runId) {
        
        // delete MS2 specific data first
        List<Integer> scanIds = loadScanIdsForRun(runId);
        for (Integer id: scanIds) {
            deleteMS2Data(id);
        }
        
        // delete all parent scans
        msScanDao.deleteScansForRun(runId);
    }

    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        
        deleteMS2Data(scanId);
        
        // delete the parent scan
        msScanDao.delete(scanId);
    }

    private void deleteMS2Data(int scanId) {
        // delete any charge independent analysis associated with the scans in the run.
        iAnalDao.deleteByScanId(scanId);
        
        // delete any scan charge information associated with the scan.
        chargeDao.deleteByScanId(scanId);
    }
}
