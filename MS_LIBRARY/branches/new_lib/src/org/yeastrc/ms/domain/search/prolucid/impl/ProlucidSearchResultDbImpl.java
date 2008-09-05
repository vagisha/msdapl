/**
 * ProlucidSearchResultDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.MsSearchResultDbImpl;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultDb;

/**
 * 
 */
public class ProlucidSearchResultDbImpl extends MsSearchResultDbImpl implements ProlucidSearchResultDb, ProlucidResultData {

    private ProlucidResultDataImpl prolucidData;
    
    public ProlucidSearchResultDbImpl() {
        prolucidData = new ProlucidResultDataImpl();
    }
    
    /**
     * @return the xCorrRank
     */
    public int getxCorrRank() {
        return prolucidData.getxCorrRank();
    }
    /**
     * @param xcorrRank the xCorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        prolucidData.setXcorrRank(xcorrRank);
    }
    /**
     * @return the spRank
     */
    public int getSpRank() {
        return prolucidData.getSpRank();
    }
    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        prolucidData.setSpRank(spRank);
    }
    /**
     * @return the deltaCN
     */
    public BigDecimal getDeltaCN() {
        return prolucidData.getDeltaCN();
    }
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        prolucidData.setDeltaCN(deltaCN);
    }
    /**
     * @return the xCorr
     */
    public BigDecimal getxCorr() {
        return prolucidData.getxCorr();
    }
    /**
     * @param xcorr the xCorr to set
     */
    public void setxCorr(BigDecimal xcorr) {
        prolucidData.setXcorr(xcorr);
    }
    /**
     * @return the sp
     */
    public BigDecimal getSp() {
        return prolucidData.getSp();
    }
    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        prolucidData.setSp(sp);
    }
    
    public Double getBinomialProbability() {
        return prolucidData.getBinomialProbability();
    }
    
    public void setBinomialProbability(Double binomialProbability) {
        prolucidData.setBinomialProbability(binomialProbability);
    }
    
    public Double getZscore() {
        return prolucidData.getZscore();
    }
    
    public void setZscore(Double zscore) {
        prolucidData.setZscore(zscore);
    }
    
    
    @Override
    public BigDecimal getCalculatedMass() {
        return prolucidData.getCalculatedMass();
    }

    public void setCalculatedMass(BigDecimal calculatedMass) {
        prolucidData.setCalculatedMass(calculatedMass);
    }
    
    @Override
    public int getMatchingIons() {
        return prolucidData.getMatchingIons();
    }

    public void setMatchingIons(int matchingIons) {
        prolucidData.setMatchingIons(matchingIons);
    }
    
    @Override
    public int getPredictedIons() {
        return prolucidData.getPredictedIons();
    }
    
    public void setPredictedIons(int predictedIons) {
        prolucidData.setPredictedIons(predictedIons);
    }

    @Override
    public ProlucidResultData getProlucidResultData() {
        return prolucidData;
    }
}
