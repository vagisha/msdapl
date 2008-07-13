/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;

/**
 * 
 */
public class ScanCharge implements MS2ScanCharge {

    private int charge;
    private BigDecimal mass;
    private List<HeaderItem> analysisItems;
    
    public ScanCharge() {
        analysisItems = new ArrayList<HeaderItem>();
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
   
    public void setMass(String mass) {
        this.mass = new BigDecimal(mass);
    }
    
    public void addAnalysisItem(String label, String value) {
        if (label == null || value == null)   return;
        analysisItems.add(new HeaderItem(label, value));
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Z\t");
        buf.append(charge);
        buf.append("\t");
        buf.append(mass);
        buf.append("\n");
        for (HeaderItem item: analysisItems) {
            buf.append("D\t");
            buf.append(item.getName());
            buf.append("\t");
            buf.append(item.getValue());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }

    @Override
    public List<? extends MS2Field> getChargeDependentAnalysisList() {
        return analysisItems;
    }
}
