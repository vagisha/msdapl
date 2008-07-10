/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.domain.db.MsScan;
import org.yeastrc.ms.domain.db.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2FileChargeIndependentAnalysis;
import org.yeastrc.ms.domain.ms2File.MS2FileScan;
import org.yeastrc.ms.domain.ms2File.MS2FileScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2FileScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO<MS2FileScan> {

    public MS2FileScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /**
     * This will return a MS2FileScan object. NO check is made to determine if 
     * the run this scan belongs to is of type MS2
     */
    public MS2FileScan load(int scanId) {
        return (MS2FileScan) queryForObject("MS2Scan.select", scanId);
    }
    
    /**
     * Returns a list of scan ids for the given run.
     */
    public List<Integer> loadScanIdsForRun(int runId) {
        
//        // first check if the original file format for this run was MS2
//        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
//        if (runDao.getRunFileFormat(runId) != RunFileFormat.MS2)
//            return new ArrayList<Integer>(0);
        
        MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
        return scanDao.loadScanIdsForRun(runId);
    }
    
    /**
     * Saves the scan along with any MS2 file format specific data.
     */
    public int save(MS2FileScan scan) {
        
        // save the parent scan first
        MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.save(scan);
        
        // save the charge independent analysis
        MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        List<MS2FileChargeIndependentAnalysis> iAnalysisList = scan.getChargeIndependentAnalysisList();
        for (MS2FileChargeIndependentAnalysis iAnalysis: iAnalysisList) {
            iAnalysis.setScanId(scanId);
            iAnalDao.save(iAnalysis);
        }
        
        // save the charge dependent analysis
        MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
        List<MS2FileScanCharge> scanChargeList = scan.getScanChargeList();
        for (MS2FileScanCharge charge: scanChargeList) {
            charge.setScanId(scanId);
            chargeDao.save(charge);
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
        MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
        scanDao.deleteScansForRun(runId);
    }

    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        
        deleteMS2Data(scanId);
        
        // delete the parent scan
        MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
        scanDao.delete(scanId);
    }

    private void deleteMS2Data(int scanId) {
        // delete any charge independent analysis associated with the scans in the run.
        MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        iAnalDao.deleteByScanId(scanId);
        
        // delete any scan charge information associated with the scan.
        MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
        chargeDao.deleteByScanId(scanId);
    }

   
   
}
