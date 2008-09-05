/**
 * ProlucidResultData.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.math.BigDecimal;

/**
 * 
 */
public interface ProlucidResultData {

    // RANKS
    /**
     * @return the xCorrRank
     */
    public abstract int getxCorrRank();
    
    /**
     * @return the spRank
     */
    public abstract int getSpRank();
    
    
    // SCORES
    /**
     * @return the sp
     */
    public abstract BigDecimal getSp();
    
    /**
     * @return the binomial probability
     */
    public abstract Double getBinomialProbability();
    
    /**
     * @return the xCorr
     */
    public abstract BigDecimal getxCorr();
    
    /**
     * @return the deltaCN
     */
    public abstract BigDecimal getDeltaCN();
    
    /**
     * @return the z-score
     */
    public abstract Double getZscore();

   
    
    public abstract BigDecimal getCalculatedMass();

    public abstract int getMatchingIons();

    public abstract int getPredictedIons();
    
}
