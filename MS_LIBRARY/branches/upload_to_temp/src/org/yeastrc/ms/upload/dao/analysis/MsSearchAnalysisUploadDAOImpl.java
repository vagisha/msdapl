package org.yeastrc.ms.upload.dao.analysis;

import java.util.List;

import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables: 
 * 1. msSearchAnalysis
 * 2. msRunSearchAnalysis (only for select queries)
 */
public class MsSearchAnalysisUploadDAOImpl extends AbstractTableCopier implements MsSearchAnalysisDAO {

    private final MsSearchAnalysisDAO analysisDao;
    private final boolean useTempTable; 
    
    
    /**
     * @param analysisDao -- DAO for the MAIN database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public MsSearchAnalysisUploadDAOImpl(MsSearchAnalysisDAO analysisDao, boolean useTempTable) {
        this.analysisDao = analysisDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public MsSearchAnalysis load(int analysisId) {
        return analysisDao.load(analysisId);
    }

    @Override
    public List<Integer> getAnalysisIdsForSearch(int searchId) {
        return analysisDao.getAnalysisIdsForSearch(searchId);
    }
    
    @Override
    public List<Integer> getSearchIdsForAnalysis(int analysisId) {
        return analysisDao.getSearchIdsForAnalysis(analysisId);
    }
    
    @Override
    public int save(MsSearchAnalysis analysis) {
        return analysisDao.save(analysis);
    }

    @Override
    public int updateAnalysisProgram(int analysisId, Program program) {
        return analysisDao.updateAnalysisProgram(analysisId, program);
    }

    @Override
    public int updateAnalysisProgramVersion(int analysisId, String versionStr) {
        return analysisDao.updateAnalysisProgramVersion(analysisId, versionStr);
    }
    
    @Override
    public void delete(int analysisId) {
       analysisDao.delete(analysisId);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required  table
            copyToMainTableDirect("msSearchAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
}
