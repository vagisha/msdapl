/**
 * ProlucidResultDataImpl.java
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
public class ProlucidResultDataBean implements ProlucidResultData {

    private BigDecimal calculatedMass;
    
    private int matchingIons = -1;
    private int predictedIons = -1;
    
    private BigDecimal sp;
    private Double binomialProbability;
    private BigDecimal xcorr;
    private Double zscore;
    private BigDecimal deltaCN;
    
    private int spRank = -1;
    private int xcorrRank = -1;
    
    
    @Override
    public BigDecimal getCalculatedMass() {
        return calculatedMass;
    }

    @Override
    public int getMatchingIons() {
        return matchingIons;
    }

    @Override
    public int getPredictedIons() {
        return predictedIons;
    }

    
    // RANKS  
    @Override
    public int getxCorrRank() {
        return xcorrRank;
    }
    
    @Override
    public int getSpRank() {
        return spRank;
    }
    
    // SCORES
    @Override
    public BigDecimal getSp() {
        return sp;
    }
    
    @Override
    public Double getBinomialProbability() {
        return binomialProbability;
    }
    
    @Override
    public BigDecimal getxCorr() {
        return xcorr;
    }

    @Override
    public Double getZscore() {
        return zscore;
    }
    
    @Override
    public BigDecimal getDeltaCN() {
        return deltaCN;
    }

    
    public void setCalculatedMass(BigDecimal calculatedMass) {
        this.calculatedMass = calculatedMass;
    }

    public void setMatchingIons(int matchingIons) {
        this.matchingIons = matchingIons;
    }

    public void setPredictedIons(int predictedIons) {
        this.predictedIons = predictedIons;
    }

    public void setSp(BigDecimal sp) {
        this.sp = sp;
    }

    public void setBinomialProbability(Double binomialProbability) {
        this.binomialProbability = binomialProbability;
    }

    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }

    public void setZscore(Double zscore) {
        this.zscore = zscore;
    }

    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }

    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }

    public void setXcorrRank(int xcorrRank) {
        this.xcorrRank = xcorrRank;
    }

}
