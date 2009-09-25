/**
 * PercolatorResult.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchResult;

/**
 * 
 */
public interface PercolatorResult extends MsSearchResult {

    public abstract int getSearchResultId();
    
    public abstract int getRunSearchAnalysisId();
    
    /**
     * @return the qvalue
     */
    public abstract double getQvalue();
    
    public abstract double getQvalueRounded();
    
    /**
     * @return the posterior error probability or -1.0 if there was no posterior probability 
     * for this result
     */
    public abstract double getPosteriorErrorProbability();
    
    public abstract double getPosteriorErrorProbabilityRounded();
    
    /**
     * @return the percolator discriminant score or null if there was no discriminant score 
     */
    public abstract Double getDiscriminantScore();
    
    public abstract Double getDiscriminantScoreRounded();
    
    
    /**
     * @return the Predicted Retention Time
     */
    public abstract BigDecimal getPredictedRetentionTime();
    
}
