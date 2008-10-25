package edu.uwpr.protinfer.filter.fdr;

import edu.uwpr.protinfer.filter.Filterable;

public interface FdrFilterable extends Filterable {

    public abstract boolean isTarget();
    
    public abstract boolean isDecoy();
    
    public abstract double getFdr();
    
}
