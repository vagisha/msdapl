/**
 * ProlucidResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;

/**
 * 
 */
public class ProlucidResultDataWrap implements ProlucidResultData {

    private int resultId;
    private ProlucidResultData data;
    
    public ProlucidResultDataWrap(ProlucidResultData data, int resultId) {
        this.data = data;
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return resultId;
    }

    @Override
    public Double getBinomialProbability() {
        return data.getBinomialProbability();
    }

    @Override
    public BigDecimal getCalculatedMass() {
        return data.getCalculatedMass();
    }

    @Override
    public BigDecimal getDeltaCN() {
        return data.getDeltaCN();
    }

    @Override
    public int getMatchingIons() {
        return data.getMatchingIons();
    }

    @Override
    public int getPredictedIons() {
        return data.getPredictedIons();
    }

    @Override
    public BigDecimal getSp() {
        return data.getSp();
    }

    @Override
    public int getSpRank() {
        return data.getSpRank();
    }

    @Override
    public Double getZscore() {
        return data.getZscore();
    }

    @Override
    public BigDecimal getxCorr() {
        return data.getxCorr();
    }

    @Override
    public int getxCorrRank() {
        return data.getxCorrRank();
    }
}
