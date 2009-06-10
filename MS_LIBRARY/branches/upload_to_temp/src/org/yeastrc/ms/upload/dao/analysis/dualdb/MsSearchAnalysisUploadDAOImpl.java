package org.yeastrc.ms.upload.dao.analysis.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.analysis.MsSearchAnalysisUploadDAO;

/**
 * Deals with the tables: 
 * 1. msSearchAnalysis
 * 2. msRunSearchAnalysis (only for select queries)
 */
public class MsSearchAnalysisUploadDAOImpl implements MsSearchAnalysisUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsSearchAnalysisUploadDAOImpl.class.getName());
    
    private final MsSearchAnalysisUploadDAO analysisDao;
    private final boolean useTempTable; 
    
    
    /**
     * @param analysisDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public MsSearchAnalysisUploadDAOImpl(MsSearchAnalysisUploadDAO analysisDao, boolean useTempTable) {
        this.analysisDao = analysisDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public List<Integer> getAnalysisIdsForSearch(int searchId) {
        return analysisDao.getAnalysisIdsForSearch(searchId);
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
    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required  table
            copier.copyToMainTableDirect("msSearchAnalysis");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msSearchAnalysis", "id"))
            return false;
        return true;
    }
    
}
