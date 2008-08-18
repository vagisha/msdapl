package org.yeastrc.ms.domain.search.sequest;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsRunSearchResult;

public interface SequestRunSearchResult extends MsRunSearchResult, SequestRunSearchResultBase {
    
}

interface SequestRunSearchResultBase {
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
    public abstract double geteValue();

    public abstract BigDecimal getCalculatedMass();

    public abstract int getMatchingIons();

    public abstract int getPredictedIons();
}
