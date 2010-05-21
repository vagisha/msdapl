/**
 * SequestRunSearchResultBase.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.math.BigDecimal;

/**
 * 
 */
public interface SequestResultData {

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

    /**
     * @return the e-value
     */
    public abstract Double getEvalue();

    public abstract BigDecimal getCalculatedMass();

    public abstract int getMatchingIons();

    public abstract int getPredictedIons();
}
