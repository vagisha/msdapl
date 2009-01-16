package org.yeastrc.ms.domain.analysis.percolator.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public class PercolatorResultDataBean implements PercolatorResultDataWId {

    private int resultId;
    private int runSearchAnalysisId;
    private double qvalue = -1.0;
    private Double discriminantScore = null;
    private double pep = -1.0;
    
    private BigDecimal predictedRT = null;
    
    @Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }

    @Override
    public int getRunSearchAnalysisId() {
        return runSearchAnalysisId;
    }

    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }

    @Override
    public int getResultId() {
        return resultId;
    }

    @Override
    public double getQvalue() {
        return qvalue;
    }

    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }
    
    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }
    
    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    public void setRunSearchAnalysisId(int analysisId) {
        this.runSearchAnalysisId = analysisId;
    }
    
    @Override
    public BigDecimal getPredictedRetentionTime() {
        return predictedRT;
    }
    
    public void setPredictedRetentionTime(BigDecimal predictedRT) {
        this.predictedRT = predictedRT;
    }
}
