/**
 * PercolatorParamsDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables: 
 * 1. PercolatorParams
 */
public class PercolatorParamsUploadDAOImpl extends AbstractTableCopier implements PercolatorParamsDAO {

    
    private final PercolatorParamsDAO paramsDao;
    private final boolean useTempTable;
    
    /**
     * @param paramsDao -- DAO for the MAIN database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public PercolatorParamsUploadDAOImpl(PercolatorParamsDAO paramsDao, boolean useTempTable) {
        this.paramsDao = paramsDao;
        this.useTempTable = useTempTable;
    }
    
    @Override
    public List<PercolatorParam> loadParams(int analysisId) {
        return paramsDao.loadParams(analysisId);
    }

    @Override
    public void saveParam(PercolatorParam param, int analysisId) {
        paramsDao.saveParam(param, analysisId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required  table
            copyToMainTableDirect("PercolatorParams");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
