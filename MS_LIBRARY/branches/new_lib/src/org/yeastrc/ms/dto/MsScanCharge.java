/**
 * MsScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;


/**
 * 
 */
public class MsScanCharge {

    private int id;         // unique id (database)
    private int scanId;     // the id (database) of the scan to which this charge corresponds
    private int charge;     // the charge state
    private float mass;     // predicted [M+H]+ (mass)
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
    /**
     * @param scanId the scanId to set
     */
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }
    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    /**
     * @return the mass
     */
    public float getMass() {
        return mass;
    }
    /**
     * @param mass the mass to set
     */
    public void setMass(float mass) {
        this.mass = mass;
    }
    
    
}
