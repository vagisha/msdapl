package org.yeastrc.ms.domain.analysis.percolator;

public interface PercolatorResultDataWId {

    public abstract int getPercolatorOutputId();
    
    public abstract int getResultId();
    
    /**
     * @return the qvalue
     */
    public abstract double getQvalue();
    
    /**
     * @return the posterior error probability or -1.0 if there was no posterior probability 
     * for this result
     */
    public abstract double getPosteriorErrorProbability();
    
    /**
     * @return the percolator discriminant score or -1.0 if there was no discriminant score. 
     */
    public abstract double getDiscriminantScore();
}
