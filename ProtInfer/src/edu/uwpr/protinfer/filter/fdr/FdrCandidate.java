package edu.uwpr.protinfer.filter.fdr;

public interface FdrCandidate  {

    public abstract boolean isTarget();
    
    public abstract boolean isDecoy();
    
    public abstract void setFdr(double fdr);
    
    public abstract double getFdr();
}
