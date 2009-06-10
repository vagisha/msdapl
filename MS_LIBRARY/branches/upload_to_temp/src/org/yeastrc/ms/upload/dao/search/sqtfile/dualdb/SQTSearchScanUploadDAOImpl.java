/**
 * SQTSearchScanUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 4, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile.dualdb;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTSearchScanUploadDAO;

/**
 * Deals with the tables:
 * 1. SQTSpectrumData
 */
public class SQTSearchScanUploadDAOImpl implements SQTSearchScanUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(SQTSearchScanUploadDAOImpl.class.getName());
    
    private final SQTSearchScanUploadDAO spectrumDao;
    private final boolean useTempTable;
    
    public SQTSearchScanUploadDAOImpl(SQTSearchScanUploadDAO spectrumDao, boolean useTempTable) {
        this.spectrumDao = spectrumDao;
        this.useTempTable = useTempTable;
    }
    
    public SQTSearchScan load(int runSearchId, int scanId, int charge) {
        return spectrumDao.load(runSearchId, scanId, charge);
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
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableFromFile("SQTSpectrumData", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("SQTSpectrumData", "runSearchID"))
            return false;
        return true;
    }
}
