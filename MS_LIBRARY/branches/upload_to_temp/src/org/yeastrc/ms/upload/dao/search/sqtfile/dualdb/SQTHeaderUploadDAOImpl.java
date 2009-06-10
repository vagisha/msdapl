/**
 * SQTHeaderDAOUploadImpl.java
 * @author Vagisha Sharma
 * Jun 4, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile.dualdb;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTHeaderUploadDAO;

/**
 * Deals with the tables:
 * 1. SQTFileHeader
 */
public class SQTHeaderUploadDAOImpl implements SQTHeaderUploadDAO, TableCopier {
    
    private static final Logger log = Logger.getLogger(SQTHeaderUploadDAOImpl.class.getName());
    
    private final SQTHeaderUploadDAO headerDao;
    private final boolean useTempTable;
    
    /**
     * @param headerDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SQTHeaderUploadDAOImpl(SQTHeaderUploadDAO headerDao, boolean useTempTable) {
        this.headerDao = headerDao;
        this.useTempTable = useTempTable;
    }
    
    public void saveSQTHeader(SQTHeaderItem header) {
        headerDao.saveSQTHeader(header);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableDirect("SQTFileHeader");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("SQTFileHeader", "id"))
            return false;
        return true;
    }
}
