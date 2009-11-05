/**
 * ProteinProperties.java
 * @author Vagisha Sharma
 * Nov 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dto;

/**
 * 
 */
public class ProteinProperties {

    private int nrseqProteinId;
    private double molecularWt;
    private double pi;
    
    
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
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
}
