package org.yeastrc.ms.domain.analysis.percolator.impl;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public class PercolatorResultDataBean implements PercolatorResultDataWId {

    private int resultId;
    private int percOutputId;
    private double qvalue = -1.0;
    private double discriminantScore = -1.0;
    private double pep = -1.0;
    
    
    @Override
    public double getDiscriminantScore() {
        return discriminantScore;
    }

    @Override
    public int getPercolatorOutputId() {
        return percOutputId;
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

    public void setDiscriminantScore(double score) {
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
    
    public void setPercolatorOutputId(int outputId) {
        this.percOutputId = outputId;
    }
}
