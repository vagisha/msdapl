/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 
 */
public class ScanCharge {

    private int charge;
    private BigDecimal mass;
    private HashMap<String, String> analysisItems;
    
    public ScanCharge() {
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
    public BigDecimal getMass() {
        return mass;
    }
    /**
     * @param mass the mass to set
     */
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }
   
    public void setMass(String mass) {
        this.mass = new BigDecimal(mass);
    }
    
    public void addAnalysisItem(String label, String value) {
        if (label == null || value == null)   return;
        analysisItems.put(label, value);
    }
    
    public HashMap<String, String> getAnalysisItems() {
        return analysisItems;
    }
    
    public String getValueForAnalysisLabel(String label) {
        return analysisItems.get(label);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Z\t");
        buf.append(charge);
        buf.append("\t");
        buf.append(mass);
        buf.append("\n");
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
