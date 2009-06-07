/**
 * MS2FileScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAOImpl;

/**
 * 
 */
public class MS2ScanUploadDAOImpl extends AbstractTableCopier implements MS2ScanDAO {

    private final MsScanUploadDAOImpl msScanDao;
    private boolean useTempTable;
    
    public MS2ScanUploadDAOImpl(MsScanUploadDAOImpl msScanDAO, boolean useTempTable) {
        this.msScanDao = msScanDAO;
        this.useTempTable = useTempTable;
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public MS2Scan load(int scanId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public MS2Scan loadScanLite(int scanId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int loadScanNumber(int scanId) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadScanIdsForRun(int runId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int numScans(int runId) {
        throw new UnsupportedOperationException();
    }
    
    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        return msScanDao.loadScanIdForScanNumRun(scanNum, runId);
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public int save(MS2ScanIn scan, int runId, int precursorScanId) {
        
        throw new UnsupportedOperationException();
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public int save(MS2ScanIn scan, int runId) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        msScanDao.delete(scanId); // triggered deletes will take care of all related tables
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required tables
            copyToMainTableFromFile("MS2FileScanCharge");
            copyToMainTableFromFile("MS2FileChargeDependentAnalysis");
            copyToMainTableFromFile("MS2FileChargeIndependentAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
