/**
 * PeptideProphetResultDataBean.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;

/**
 * 
 */
public class PeptideProphetResultDataBean implements PeptideProphetResultDataWId {

    private int resultId;
    private int runSearchAnalysisId;
    private double probability = -1.0;
    private double fVal = -1.0; // TODO is this always positive?
    private double massDiff = 0.0;
    private int ntt;
    private int nmc;
    private String allNttProb;
    
    @Override
    public String getAllNttProb() {
        return allNttProb;
    }
    
    public void setAllNttProb(String allNttProb) {
        this.allNttProb = allNttProb;
    }

    @Override
    public double getMassDifference() {
        return massDiff;
    }
    
    public void setMassDifference(double massDiff) {
        this.massDiff = massDiff;
    }

    @Override
    public int getNumMissedCleavages() {
        return nmc;
    }
    
    public void setNumMissedCleavages(int nmc) {
        this.nmc = nmc;
    }

    @Override
    public int getNumTrypticTermini() {
        return ntt;
    }
    
    public void setNumTrypticTermini(int ntt) {
        this.ntt = ntt;
    }

    @Override
    public double getProbability() {
        return probability;
    }
    
    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    @Override
    public int getRunSearchAnalysisId() {
        return runSearchAnalysisId;
    }
    
    public void setRunSearchAnalysisId(int analysisId) {
        this.runSearchAnalysisId = analysisId;
    }

    @Override
    public double getfVal() {
        return fVal;
    }
    
    public void setfVal(double fVal) {
        this.fVal = fVal;
    }

}
