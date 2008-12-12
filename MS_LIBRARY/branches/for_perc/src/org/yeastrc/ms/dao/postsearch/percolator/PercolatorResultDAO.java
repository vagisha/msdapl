package org.yeastrc.ms.dao.postsearch.percolator;

import java.util.List;

import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResult;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResultDataWId;

public interface PercolatorResultDAO {

    
    public abstract PercolatorResult load(int msResultId);
    
    
    public abstract List<Integer> loadResultIdsForPercolatorOutput(int percOutputId);
    
    
    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId);
    
    
    public abstract int save(PercolatorResultDataWId data);
    
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
}
