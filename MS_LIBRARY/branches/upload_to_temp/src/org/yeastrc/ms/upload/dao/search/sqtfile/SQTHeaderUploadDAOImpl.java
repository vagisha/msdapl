/**
 * SQTHeaderDAOUploadImpl.java
 * @author Vagisha Sharma
 * Jun 4, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. SQTFileHeader
 */
public class SQTHeaderUploadDAOImpl extends AbstractTableCopier implements SQTHeaderDAO {

    private final SQTHeaderDAO headerDao;
    private final boolean useTempTable;
    
    /**
     * @param headerDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SQTHeaderUploadDAOImpl(SQTHeaderDAO headerDao, boolean useTempTable) {
        this.headerDao = headerDao;
        this.useTempTable = useTempTable;
    }
    
    public List<SQTHeaderItem> loadSQTHeadersForRunSearch(int runSearchId) {
        return headerDao.loadSQTHeadersForRunSearch(runSearchId);
    }
    
    
    public void saveSQTHeader(SQTHeaderItem header) {
        headerDao.saveSQTHeader(header);
    }
    
    public void deleteSQTHeadersForRunSearch(int runSearchId) {
        headerDao.deleteSQTHeadersForRunSearch(runSearchId);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableDirect("SQTFileHeader");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
