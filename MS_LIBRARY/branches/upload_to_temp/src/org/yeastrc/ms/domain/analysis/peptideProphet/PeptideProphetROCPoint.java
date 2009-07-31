/**
 * PeptideProphetROCPoint.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

public class PeptideProphetROCPoint {
    
    private int searchAnalysisId;
    private double sensitivity;
    private double error;
    private double probability;
    private int numCorrect;
    private int numIncorrect;
    
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }
    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
    }
    public double getSensitivity() {
        return sensitivity;
    }
    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }
    public double getError() {
        return error;
    }
    public void setError(double error) {
        this.error = error;
    }
    public double getProbability() {
        return probability;
    }
    public void setProbability(double probability) {
        this.probability = probability;
    }
    public int getNumCorrect() {
        return numCorrect;
    }
    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }
    public int getNumIncorrect() {
        return numIncorrect;
    }
    public void setNumIncorrect(int numIncorrect) {
        this.numIncorrect = numIncorrect;
    }
}