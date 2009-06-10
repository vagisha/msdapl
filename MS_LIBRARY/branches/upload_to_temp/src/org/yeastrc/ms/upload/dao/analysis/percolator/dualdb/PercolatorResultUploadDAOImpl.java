/**
 * PercolatorResultUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.percolator.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorResultUploadDAO;

/**
 * Deals with the tables: 
 * 1. PercolatorResult
 */
public class PercolatorResultUploadDAOImpl implements PercolatorResultUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(PercolatorResultUploadDAOImpl.class.getName());
    
    private final PercolatorResultUploadDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public PercolatorResultUploadDAOImpl(PercolatorResultUploadDAO resultDao, boolean useTempTable) {
        this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }
    
    @Override
    public void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList) {
        resultDao.saveAllPercolatorResultData(dataList);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required  table
            copier.copyToMainTableFromFile("PercolatorResult", true); // disable keys on main database table before copying
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("PercolatorResult", "resultID"))
            return false;
        return true;
    }
}
