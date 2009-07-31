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
    
    public void setRocPoints(List<PeptideProphetROCPoint> rocPoints) {
        this.rocPoints = rocPoints;
    }

    public double getMinProbabilityForError(double error) {
        double closestError = Double.MIN_VALUE;
        double probability = -1.0;
        
        for(PeptideProphetROCPoint point: rocPoints) {
            if(probability == -1.0) {
                closestError = point.getError();
                probability = point.getProbability();
                continue;
            }
            double diff = Math.abs(error - point.getError());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getError();
                probability = point.getProbability();
            }
        }
        return probability;
    }
    
    public double getClosestError(double error) {
        
        double closestError = rocPoints.get(0).getError();
        
        for(PeptideProphetROCPoint point: rocPoints) {
            double diff = Math.abs(error - point.getError());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getError();
            }
        }
        return closestError;
    }
}


