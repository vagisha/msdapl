package org.yeastrc.ms.domain.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.IMsSearchResult;

public interface ISQTSearchResult extends IMsSearchResult {

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