/**
 * Peak.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 */
public class Peak implements Serializable{

    private double intensity;
    private double mz;
    
    public Peak() {}
    
    public Peak(double mz, double intensity) {
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
    public double getMz() {
        return mz;
    }
    /**
     * @param mz the mz to set
     */
    public void setMz(double mz) {
        this.mz = mz;
    }
    
    
}
