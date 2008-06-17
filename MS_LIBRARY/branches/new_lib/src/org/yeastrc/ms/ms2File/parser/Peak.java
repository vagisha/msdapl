/**
 * Peak.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.parser;

/**
 * 
 */
public class Peak {

    private double intensity;
    private float mz;
    
    public Peak() {}
    
    public Peak(float mz, double intensity) {
        this.mz = mz;
        this.intensity = intensity;
    }
    /**
     * @return the intensity
     */
    public double getIntensity() {
        return intensity;
    }
    /**
     * @param intensity the intensity to set
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
    /**
     * @return the mz
     */
    public float getMz() {
        return mz;
    }
    /**
     * @param mz the mz to set
     */
    public void setMz(float mz) {
        this.mz = mz;
    }
    
    
}
