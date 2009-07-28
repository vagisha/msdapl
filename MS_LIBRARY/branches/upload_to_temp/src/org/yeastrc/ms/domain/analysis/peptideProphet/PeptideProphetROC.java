/**
 * PeptideProphet_ROC.java
 * @author Vagisha Sharma
 * Jul 23, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class PeptideProphetROC {

    private int searchAnalysisId;
    private List<PeptideProphetROCPoint> rocPoints;
    
    public PeptideProphetROC() {
        rocPoints = new ArrayList<PeptideProphetROCPoint>();
    }
    
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
    }

    public List<PeptideProphetROCPoint> getRocPoints() {
        return rocPoints;
    }
    
    public void addRocPoint(PeptideProphetROCPoint point) {
        this.rocPoints.add(point);
    }
    
    public double getMinProbabilityForError(double error) {
        double closestError = Double.MIN_VALUE;
        double probability = -1.0;
        
        for(PeptideProphetROCPoint point: rocPoints) {
            if(probability == -1.0) {
                closestError = point.error;
                probability = point.probability;
                continue;
            }
            double diff = Math.abs(error - point.error);
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.error;
                probability = point.probability;
            }
        }
        return probability;
    }
    
    public double getClosestError(double error) {
        
        double closestError = rocPoints.get(0).getError();
        
        for(PeptideProphetROCPoint point: rocPoints) {
            double diff = Math.abs(error - point.error);
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.error;
            }
        }
        return closestError;
    }
    
    public static class PeptideProphetROCPoint {
        
        private double sensitivity;
        private double error;
        private double probability;
        private int numCorrect;
        private int numIncorrect;
        
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
}


