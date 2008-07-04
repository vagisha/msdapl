package org.yeastrc.ms.dto.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.dto.MsPeptideSearchResult;

public class SQTSearchResult extends MsPeptideSearchResult {

    private int xCorrRank;
    private int spRank;
    private BigDecimal deltaCN;
    private BigDecimal xCorr;
    private BigDecimal sp;
    
    /**
     * @return the xCorrRank
     */
    public int getXCorrRank() {
        return xCorrRank;
    }
    /**
     * @param corrRank the xCorrRank to set
     */
    public void setXCorrRank(int corrRank) {
        xCorrRank = corrRank;
    }
    /**
     * @return the spRank
     */
    public int getSpRank() {
        return spRank;
    }
    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }
    /**
     * @return the deltaCN
     */
    public BigDecimal getDeltaCN() {
        return deltaCN;
    }
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }
    /**
     * @return the xCorr
     */
    public BigDecimal getXCorr() {
        return xCorr;
    }
    /**
     * @param corr the xCorr to set
     */
    public void setXCorr(BigDecimal corr) {
        xCorr = corr;
    }
    /**
     * @return the sp
     */
    public BigDecimal getSp() {
        return sp;
    }
    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        this.sp = sp;
    }
}
