/**
 * ProteinProperties.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */

package org.yeastrc.www.proteinfer;


/**
 * 
 */
public final class ProteinProperties {

    private int pinferProteinId;
    private int nrseqId;
    private int proteinGroupId;
    
    private double molecularWt;
    private double pi;
    
    public ProteinProperties(int pinferProteinId) {
        this.pinferProteinId = pinferProteinId;
    }
    
    public int getPinferProteinId() {
        return pinferProteinId;
    }

    public void setPinferProteinId(int pinferProteinId) {
        this.pinferProteinId = pinferProteinId;
    }

    public int getNrseqId() {
        return nrseqId;
    }

    public void setNrseqId(int nrseqId) {
        this.nrseqId = nrseqId;
    }

    public int getProteinGroupId() {
        return proteinGroupId;
    }
    
    public void setProteinGroupId(int proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }

    public double getMolecularWt() {
        return molecularWt;
    }

    public void setMolecularWt(double molecularWt) {
        this.molecularWt = molecularWt;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }
}
