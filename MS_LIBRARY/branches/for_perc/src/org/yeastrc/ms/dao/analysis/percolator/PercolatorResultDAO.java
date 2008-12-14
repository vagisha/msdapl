package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public interface PercolatorResultDAO {

    
    public abstract PercolatorResult load(int resultId);
    
    
    public abstract List<Integer> loadResultIdsWithQvalueThreshold(int analysisId, double qvalue);
    
    
    public abstract List<Integer> loadResultIdsWithPepThreshold(int analysisId, double pep);
    
    
    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId);
    
    
    public abstract List<Integer> loadResultIdsForPercolatorAnalysis(int analysisId);
    
    
    public abstract void save(PercolatorResultDataWId data);
    
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
}
