/**
 * ProteinProphetROCPoint.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

public class ProteinProphetROCPoint {
    
    private int searchAnalysisId;
    private double sensitivity;
    private double falsePositiveErrorRate;
    private double minProbability;
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
        return falsePositiveErrorRate;
    }
    public void setError(double error) {
        this.falsePositiveErrorRate = error;
    }
    public double getMinProbability() {
        return minProbability;
    }
    public void setMinProbability(double probability) {
        this.minProbability = probability;
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