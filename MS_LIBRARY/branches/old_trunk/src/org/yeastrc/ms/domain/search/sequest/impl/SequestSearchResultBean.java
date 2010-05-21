package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

public class SequestSearchResultBean extends SearchResultBean implements SequestSearchResult {

    private SequestResultDataBean sequestData;
    
    public SequestSearchResultBean() {
        sequestData = new SequestResultDataBean();
    }
    
    /**
     * @param xcorrRank the xCorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        sequestData.setXcorrRank(xcorrRank);
    }
    
    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        sequestData.setSpRank(spRank);
    }
    
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        sequestData.setDeltaCN(deltaCN);
    }
    
    /**
     * @param xcorr the xCorr to set
     */
    public void setxCorr(BigDecimal xcorr) {
        sequestData.setXcorr(xcorr);
    }
    
    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        sequestData.setSp(sp);
    }
    
    public void setEvalue(Double evalue) {
        sequestData.setEvalue(evalue);
    }

    public void setCalculatedMass(BigDecimal calculatedMass) {
        sequestData.setCalculatedMass(calculatedMass);
    }
    
    public void setMatchingIons(int matchingIons) {
        sequestData.setMatchingIons(matchingIons);
    }
    
    public void setPredictedIons(int predictedIons) {
        sequestData.setPredictedIons(predictedIons);
    }

    @Override
    public SequestResultData getSequestResultData() {
        return sequestData;
    }
}
