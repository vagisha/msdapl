/**
 * ProlucidSearchResultDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;

/**
 * 
 */
public class ProlucidSearchResultBean extends SearchResultBean implements ProlucidSearchResult {

    private ProlucidResultDataBean prolucidData;
    
    public ProlucidSearchResultBean() {
        prolucidData = new ProlucidResultDataBean();
    }
    
    /**
     * @param xcorrRank the xCorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        prolucidData.setXcorrRank(xcorrRank);
    }
   
    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        prolucidData.setSpRank(spRank);
    }
    
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        prolucidData.setDeltaCN(deltaCN);
    }
    
    /**
     * @param xcorr the xCorr to set
     */
    public void setxCorr(BigDecimal xcorr) {
        prolucidData.setXcorr(xcorr);
    }
    
    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        prolucidData.setSp(sp);
    }
    
    public void setBinomialProbability(Double binomialProbability) {
        prolucidData.setBinomialProbability(binomialProbability);
    }
   
    public void setZscore(Double zscore) {
        prolucidData.setZscore(zscore);
    }
    
    public void setCalculatedMass(BigDecimal calculatedMass) {
        prolucidData.setCalculatedMass(calculatedMass);
    }
   
    public void setMatchingIons(int matchingIons) {
        prolucidData.setMatchingIons(matchingIons);
    }
   
    public void setPredictedIons(int predictedIons) {
        prolucidData.setPredictedIons(predictedIons);
    }

    @Override
    public ProlucidResultData getProlucidResultData() {
        return prolucidData;
    }
}
