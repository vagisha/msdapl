/**
 * Peaks.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.io.Serializable;

/**
 * 
 */
public class Peaks implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
//    List <Peak> peaks;
    double[][] peaks;
    
    public Peaks() {
//        peaks = new ArrayList<Peak>();
    }
    
    public int getPeaksCount() {
//        return peaks.size();
        return peaks.length;
    }
    
    public double[] getPeakAtIndex(int index) {
        if (index < 0 || index >= getPeaksCount())
            throw new ArrayIndexOutOfBoundsException("Invalid peak index: "+index);
        double[] peak = new double[2];
        peak[0] = peaks[index][0];
        peak[1] = peaks[index][1];
        return peak;
    }
    
    public void setPeaks(double[][] peaks) {
        if (peaks != null)
            this.peaks = peaks;
    }
}
