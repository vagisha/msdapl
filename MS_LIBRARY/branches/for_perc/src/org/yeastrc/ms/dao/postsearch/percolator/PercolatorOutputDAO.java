package org.yeastrc.ms.dao.postsearch.percolator;

import java.util.List;

import org.yeastrc.ms.domain.postsearch.percolator.PercolatorOutput;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorOutputIn;

public interface PercolatorOutputDAO {

    public abstract int save(PercolatorOutputIn percOutput, int percId, int runSearchId);
    
    public abstract PercolatorOutput load(int perOutputId);
    
    public abstract List<Integer> loadOutputIds(int percId);
    
}
