/**
 * SQTSearchScanUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 4, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. SQTSpectrumData
 */
public class SQTSearchScanUploadDAOImpl extends AbstractTableCopier implements SQTSearchScanDAO {

    private final SQTSearchScanDAO spectrumDao;
    private final boolean useTempTable;
    
    public SQTSearchScanUploadDAOImpl(SQTSearchScanDAO spectrumDao, boolean useTempTable) {
        this.spectrumDao = spectrumDao;
        this.useTempTable = useTempTable;
    }
    
    public SQTSearchScan load(int runSearchId, int scanId, int charge) {
        return spectrumDao.load(runSearchId, scanId, charge);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void save(SQTSearchScan scanData) {
        throw new UnsupportedOperationException();
    }
    
    public void deleteForRunSearch(int runSearchId) {
        spectrumDao.deleteForRunSearch(runSearchId);
    }

    @Override
    public void delete(int runSearchId, int scanId, int charge) {
        spectrumDao.delete(runSearchId, scanId, charge);
    }

    @Override
    public void saveAll(List<SQTSearchScan> scanDataList) {
        spectrumDao.saveAll(scanDataList);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        this.spectrumDao.disableKeys();
    }

    @Override
    public void enableKeys() throws SQLException {
        this.spectrumDao.enableKeys();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableFromFile("SQTSpectrumData", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
