package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public interface PercolatorResultDAO {

    
    public abstract PercolatorResult load(int resultId);
    
    
    public abstract List<PercolatorResult> loadTopPercolatorResultsN(int runSearchAnalysisId, 
                                Double qvalue, Double pep, Double discriminantScore, boolean getDynaResMods);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId);
    
    
    public abstract List<Integer> loadResultIdsForAnalysis(int analysisId);
    
    
    public abstract void save(PercolatorResultDataWId data);
    
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
    public abstract int numResults(int runSearchAnalysisId);
}
