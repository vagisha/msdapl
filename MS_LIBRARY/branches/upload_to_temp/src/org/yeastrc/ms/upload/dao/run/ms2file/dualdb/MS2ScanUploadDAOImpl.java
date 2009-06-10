/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanUploadDAO;

/**
 * 
 */
public class MS2ScanUploadDAOImpl implements MS2ScanUploadDAO {

    private static final Logger log = Logger.getLogger(MS2ScanUploadDAOImpl.class.getName());
    
    private final MS2ScanUploadDAO ms2ScanDao;
    private boolean useTempTable;
    
    public MS2ScanUploadDAOImpl(MS2ScanUploadDAO ms2ScanDAO, boolean useTempTable) {
        this.ms2ScanDao = ms2ScanDAO;
        this.useTempTable = useTempTable;
    }

    @Override
    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        return ms2ScanDao.loadScanIdForScanNumRun(scanNum, runId);
    }
    
    @Override
    public int save(MS2ScanIn scan, int runId, int precursorScanId) {
        return ms2ScanDao.save(scan, runId, precursorScanId);
    }
    
    @Override
    public int save(MS2ScanIn scan, int runId) {
        return ms2ScanDao.save(scan, runId);
    }
    
    @Override
    public <T extends MsScanIn> List<Integer> save(List<T> scans, int runId) {
        return ms2ScanDao.save(scans, runId);
    }
    
    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        ms2ScanDao.delete(scanId); // triggered deletes will take care of all related tables
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required tables
            copier.copyToMainTableFromFile("MS2FileScanCharge");
            copier.copyToMainTableFromFile("MS2FileChargeDependentAnalysis");
            copier.copyToMainTableFromFile("MS2FileChargeIndependentAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
}
