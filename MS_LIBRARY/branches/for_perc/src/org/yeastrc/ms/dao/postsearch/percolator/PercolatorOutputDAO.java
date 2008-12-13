package org.yeastrc.ms.dao.postsearch.percolator;

import java.util.List;

import org.yeastrc.ms.domain.postsearch.percolator.PercolatorOutput;

public interface PercolatorOutputDAO {

    public abstract int save(PercolatorOutput percOutput);
    
    public abstract PercolatorOutput load(int perOutputId);
    
    public abstract List<Integer> loadOutputIds(int percId);
    
    public abstract void delete(int percOutputId);
    
}
