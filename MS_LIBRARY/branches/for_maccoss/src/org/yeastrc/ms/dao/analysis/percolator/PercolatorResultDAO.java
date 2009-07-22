package org.yeastrc.ms.dao.analysis.percolator;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;

public interface PercolatorResultDAO {

    
    public abstract PercolatorResult load(int resultId);
    
    
    public abstract List<PercolatorResult> loadTopPercolatorResultsN(int runSearchAnalysisId, 
                                Double qvalue, Double pep, Double discriminantScore, boolean getDynaResMods);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, 
                            PercolatorResultFilterCriteria filterCriteria, 
                            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadResultIdsForSearchAnalysis(int searchAnalysisId, 
            PercolatorResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadResultIdsForSearchAnalysisUniqPeptide(int searchAnalysisId, 
            PercolatorResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId);
    
    
    public abstract List<Integer> loadResultIdsForAnalysis(int analysisId);
    
    public abstract List<Integer> loadResultIdsForAnalysis(int analysisId, int limit, int offset);
    
    
    public abstract void save(PercolatorResultDataWId data);
    
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
    public abstract int numRunAnalysisResults(int runSearchAnalysisId);
    
    public abstract int numAnalysisResults(int searchAnalysisId);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}
