/**
 * PercolatorParamsDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.percolator.dualdb;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorParamsUploadDAO;

/**
 * Deals with the tables: 
 * 1. PercolatorParams
 */
public class PercolatorParamsUploadDAOImpl implements PercolatorParamsUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(PercolatorParamsUploadDAOImpl.class.getName());
    
    private final PercolatorParamsUploadDAO paramsDao;
    private final boolean useTempTable;
    
    /**
     * @param paramsDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public PercolatorParamsUploadDAOImpl(PercolatorParamsUploadDAO paramsDao, boolean useTempTable) {
        this.paramsDao = paramsDao;
        this.useTempTable = useTempTable;
    }
    

    @Override
    public void saveParam(PercolatorParam param, int analysisId) {
        paramsDao.saveParam(param, analysisId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required  table
            copier.copyToMainTableDirect("PercolatorParams");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("PercolatorParams", "id"))
            return false;
        return true;
    }
}
