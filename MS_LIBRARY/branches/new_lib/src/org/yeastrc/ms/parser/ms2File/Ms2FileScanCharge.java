/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.util.HashMap;

/**
 * 
 */
public class Ms2FileScanCharge {

    private int charge;
    private float mass;
    private HashMap<String, String> analysisItems;
    
    public Ms2FileScanCharge() {
        analysisItems = new HashMap<String, String>();
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
   
    public void addAnalysisItem(String label, String value) {
        if (label == null || value == null)   return;
        analysisItems.put(label, value);
    }
    
    public String getValueForAnalysisLavel(String label) {
        return analysisItems.get(label);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Z\t");
        buf.append(charge);
        buf.append("\t");
        buf.append(mass);
        for (String label: analysisItems.keySet()) {
            buf.append("D\t");
            buf.append(label);
            buf.append("\t");
            buf.append(analysisItems.get(label));
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }
}
