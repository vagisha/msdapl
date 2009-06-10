/**
 * MsRunSearchAnalysisUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.dualdb;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.analysis.MsRunSearchAnalysisUploadDAO;

/**
 * Deals with the tables: 
 * 1. msRunSearchAnalysis
 */
public class MsRunSearchAnalysisUploadDAOImpl implements MsRunSearchAnalysisUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsRunSearchAnalysisUploadDAOImpl.class.getName());
    
    private final MsRunSearchAnalysisUploadDAO rsAnalysisDao;
    private final boolean useTempTable;
    
    /**
     * @param rsAnalysisDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public MsRunSearchAnalysisUploadDAOImpl(MsRunSearchAnalysisUploadDAO rsAnalysisDao, boolean useTempTable) {
        this.rsAnalysisDao = rsAnalysisDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public int save(MsRunSearchAnalysis runSearchAnalysis) {
        return rsAnalysisDao.save(runSearchAnalysis);
    }
    
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required  table
            copier.copyToMainTableDirect("msRunSearchAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }

    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msRunSearchAnalysis", "id"))
            return false;
        return true;
    }
}
