/**
 * ProteinProphetRoc.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinProphetROC {

    private int proteinferId;
    private List<ProteinProphetROCPoint> rocPoints;
    
    public ProteinProphetROC() {
        rocPoints = new ArrayList<ProteinProphetROCPoint>();
    }
    
    public int getProteinferId() {
        return proteinferId;
    }

    public void setProteinferId(int proteinferId) {
        this.proteinferId = proteinferId;
    }

    public List<ProteinProphetROCPoint> getRocPoints() {
        return rocPoints;
    }
    
    public void addRocPoint(ProteinProphetROCPoint point) {
        this.rocPoints.add(point);
    }
    
    public void setRocPoints(List<ProteinProphetROCPoint> rocPoints) {
        this.rocPoints = rocPoints;
    }

    public double getMinProbabilityForError(double error) {
        double closestError = Double.MIN_VALUE;
        double probability = -1.0;
        
        for(ProteinProphetROCPoint point: rocPoints) {
            if(probability == -1.0) {
                closestError = point.getFalsePositiveErrorRate();
                probability = point.getMinProbability();
                continue;
            }
            double diff = Math.abs(error - point.getFalsePositiveErrorRate());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getFalsePositiveErrorRate();
                probability = point.getMinProbability();
            }
        }
        return probability;
    }
    
    public double getClosestError(double error) {
        
        double closestError = rocPoints.get(0).getFalsePositiveErrorRate();
        
        for(ProteinProphetROCPoint point: rocPoints) {
            double diff = Math.abs(error - point.getFalsePositiveErrorRate());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getFalsePositiveErrorRate();
            }
        }
        return closestError;
    }
}
