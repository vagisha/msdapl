/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dto.IMsRun;
import org.yeastrc.ms.dto.IMsScan;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;
import org.yeastrc.ms.dto.ms2File.MS2FileChargeIndependentAnalysis;
import org.yeastrc.ms.dto.ms2File.MS2FileScan;
import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO {

    public MsScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(IMsScan scan) {
        int scanId = saveAndReturnId("MsScan.insert", scan);
        scan.setId(scanId);
        
        // save any file-format specific information
        if (scan instanceof MS2FileScan) {
            saveMS2FileScan((MS2FileScan)scan);
        }
        return scanId;
    }
    
    private void saveMS2FileScan(MS2FileScan scan) {
        // save the charge independent analysis
        List<MS2FileChargeIndependentAnalysis> iAnalysisList = scan.getChargeIndependentAnalysisList();
        MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        for (MS2FileChargeIndependentAnalysis iAnalysis: iAnalysisList) {
            iAnalysis.setScanId(scan.getId());
            iAnalDao.save(iAnalysis);
        }
        
        // save the charge dependent analysis
        List<MS2FileScanCharge> scanChargeList = scan.getScanChargeList();
        MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
        for (MS2FileScanCharge charge: scanChargeList) {
            charge.setScanId(scan.getId());
            chargeDao.save(charge);
        }
        
    }

    public IMsScan load(int scanId) {
        return (IMsScan) queryForObject("MsScan.select", scanId);
    }

    public IMsScan loadForFormat(int scanId) {
       IMsScan scan = load(scanId);
       
       // determine the file format for the run this scan belongs to
       int runId = scan.getRunId();
       MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
       
       if (runDao.getRunFileFormat(runId) == RunFileFormat.MS2) {
           scan = loadMS2FileScan(scan);
       }
       return scan;
    }
    
    private IMsScan loadMS2FileScan(IMsScan scan) {
        MS2FileScan ms2Scan = new MS2FileScan(scan);
        
        // load the charge independent headers
        MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
        ms2Scan.setChargeIndependentAnalysisList(iAnalDao.loadAnalysisForScan(scan.getId()));
        
        // load the scan charge information
        MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
        ms2Scan.setScanChargeList(chargeDao.loadChargesForScan(scan.getId()));
        
        return ms2Scan;
    }

    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }

    public void deleteScansForRun(int runId) {
        // if there is any file-format specific information, delete that first.
        deleteFileFormatSpecificData(runId);
        
        // now delete the scans for the run
        delete("MsScan.deleteByRunId", runId);
    }

    @Override
    public void deleteScansForRuns(List<Integer> runIds) {
        if (runIds == null || runIds.size() == 0)   return;
        
        // delete file-format specific information first
        for (Integer runId: runIds)
            deleteFileFormatSpecificData(runId);
        
        // now delete all the scans for all the runs.
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("runIdList", runIds);
        delete("MsScan.deleteByRunIds", map);
    }

    private void deleteFileFormatSpecificData(Integer runId) {
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        
        if (runDao.getRunFileFormat(runId) == RunFileFormat.MS2) {
            
            // delete any charge independent analysis associated with the scans in the run.
            MS2FileChargeIndependentAnalysisDAO iAnalDao = DAOFactory.instance().getMs2FileChargeIAnalysisDAO();
            iAnalDao.deleteByRunId(runId);
            
            // delete any scan charge information associated with the scan.
            MS2FileScanChargeDAO chargeDao = DAOFactory.instance().getMS2FileScanChargeDAO();
            chargeDao.deleteByRunId(runId);
        }
    }

    
}
