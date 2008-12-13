package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public interface PercolatorResultDAO {

    
    public abstract PercolatorResult load(int msResultId);
    
    
    public abstract List<Integer> loadResultIdsForPercolatorOutput(int percOutputId);
    
    
    public abstract List<Integer> loadResultIdsWithQvalueThreshold(int percOutputId, double qvalue);
    
    
    public abstract List<Integer> loadResultIdsWithPepThreshold(int percOutputId, double pep);
    
    
    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId);
    
    
    public abstract int save(PercolatorResultDataWId data);
    
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
}
