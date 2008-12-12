/**
 * PercolatorResult.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.postsearch.percolator;

import org.yeastrc.ms.domain.search.MsSearchResult;

/**
 * 
 */
public interface PercolatorResult extends MsSearchResult {

    
    public abstract int getPercolatorOutputId();
    
    /**
     * @return the qvalue
     */
    public abstract double qetQvalue();
    
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
