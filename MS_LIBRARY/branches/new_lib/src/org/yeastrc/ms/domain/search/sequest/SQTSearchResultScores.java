/**
 * SQTSearchResultScores.java
 * @author Vagisha Sharma
 * Jul 20, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.math.BigDecimal;

/**
 * 
 */
public interface SQTSearchResultScores {

    /**
     * @return the xCorrRank
     */
    public abstract int getxCorrRank();

    /**
     * @return the spRank
     */
    public abstract int getSpRank();

    /**
     * @return the deltaCN
     */
    public abstract BigDecimal getDeltaCN();

    /**
     * @return the xCorr
     */
    public abstract BigDecimal getxCorr();

    /**
     * @return the sp
     */
    public abstract BigDecimal getSp();
}
