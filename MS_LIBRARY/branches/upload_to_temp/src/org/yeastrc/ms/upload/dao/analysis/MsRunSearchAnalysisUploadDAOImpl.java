/**
 * MsRunSearchAnalysisUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis;

import java.util.List;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables: 
 * 1. msRunSearchAnalysis
 */
public class MsRunSearchAnalysisUploadDAOImpl extends AbstractTableCopier implements MsRunSearchAnalysisDAO {

    private final MsRunSearchAnalysisDAO rsAnalysisDao;
    private final boolean useTempTable;
    
    /**
     * @param rsAnalysisDao -- DAO for the MAIN database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public MsRunSearchAnalysisUploadDAOImpl(MsRunSearchAnalysisDAO rsAnalysisDao, boolean useTempTable) {
        this.rsAnalysisDao = rsAnalysisDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public int save(MsRunSearchAnalysis runSearchAnalysis) {
        return rsAnalysisDao.save(runSearchAnalysis);
    }
    
    @Override
    public MsRunSearchAnalysis load(int runSearchAnalysisId) {
        return rsAnalysisDao.load(runSearchAnalysisId);
    }
    
    @Override
    public List<Integer> getRunSearchAnalysisIdsForAnalysis(int analysisId) {
        return rsAnalysisDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public String loadFilenameForRunSearchAnalysis(int runSearchAnalysisId) {
       throw new UnsupportedOperationException();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required  table
            copyToMainTableDirect("msRunSearchAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }

}
