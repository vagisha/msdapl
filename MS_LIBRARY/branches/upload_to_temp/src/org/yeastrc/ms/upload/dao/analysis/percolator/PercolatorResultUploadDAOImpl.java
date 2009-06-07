/**
 * PercolatorResultUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 04, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables: 
 * 1. PercolatorResult
 */
public class PercolatorResultUploadDAOImpl extends AbstractTableCopier implements PercolatorResultDAO {

    private final PercolatorResultDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param resultDao -- DAO for the MAIN database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for msRunEnzyme and msSearchEnzyme tables.
     */
    public PercolatorResultUploadDAOImpl(PercolatorResultDAO resultDao, boolean useTempTable) {
        this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public PercolatorResult load(int msResultId) {
        return resultDao.load(msResultId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId) {
        return resultDao.loadResultIdsForRunSearchAnalysis(runSearchAnalysisId);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset) {
        return resultDao.loadResultIdsForRunSearchAnalysis(runSearchAnalysisId, limit, offset);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId) {
        return resultDao.loadResultIdsForRunSearchAnalysisScan(runSearchAnalysisId, scanId);
    }
    
    @Override
    public List<Integer> loadResultIdsForAnalysis(int analysisId) {
        return resultDao.loadResultIdsForAnalysis(analysisId);
    }
    
    @Override
    public List<Integer> loadResultIdsForAnalysis(int searchAnalyisId, int limit, int offset) {
        return resultDao.loadResultIdsForAnalysis(searchAnalyisId, limit, offset);
    }
    
    @Override
    public int numRunAnalysisResults(int runSearchAnalysisId) {
        return resultDao.numRunAnalysisResults(runSearchAnalysisId);
    }
    
    @Override
    public int numAnalysisResults(int searchAnalysisId) {
        return resultDao.numAnalysisResults(searchAnalysisId);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void save(PercolatorResultDataWId data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteResultsForRunSearchAnalysis(int runSearchAnalysisId) {
        resultDao.deleteResultsForRunSearchAnalysis(runSearchAnalysisId);
    }
    
    @Override
    public void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList) {
        resultDao.saveAllPercolatorResultData(dataList);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearchAnalysis(int searchAnalysisId,
            PercolatorResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForRunSearchAnalysis(
            int runSearchAnalysisId,
            PercolatorResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<PercolatorResult> loadTopPercolatorResultsN(
            int runSearchAnalysisId, Double qvalue, Double pep, Double discriminantScore,
            boolean getDynaResMods) {
        throw new UnsupportedOperationException();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required  table
            copyToMainTableFromFile("PercolatorResult", true); // disable keys on main database table before copying
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
