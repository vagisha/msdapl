/**
 * SequestResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.sequest.SequestResultData;


/**
 * 
 */
public class SequestResultDataWrap implements SequestResultData {

    private int resultId;
    private SequestResultData data;
    
    public SequestResultDataWrap(SequestResultData data, int resultId) {
        this.data = data;
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return resultId;
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
    public Double getEvalue() {
        return data.getEvalue();
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
    public BigDecimal getxCorr() {
        return data.getxCorr();
    }

    @Override
    public int getxCorrRank() {
        return data.getxCorrRank();
    }

    @Override
    public BigDecimal getDeltaCNstar() {
        return data.getDeltaCNstar();
    }
}
