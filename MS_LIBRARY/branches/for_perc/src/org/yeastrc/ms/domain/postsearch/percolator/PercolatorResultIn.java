/**
 * PercolatorResultIn.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.postsearch.percolator;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;

/**
 * 
 */
public interface PercolatorResultIn {

    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
    /**
     * @return the charge
     */
    public abstract int getCharge();
    
    /**
     * The peptide
     * @return
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    
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
