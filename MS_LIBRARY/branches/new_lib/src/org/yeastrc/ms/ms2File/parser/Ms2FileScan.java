/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.Peak;
import org.yeastrc.ms.Peaks;

/**
 * 
 */
public class Ms2FileScan {

    private int startScan;
    private int endScan;
    
    private float precursorMz;
    
    private Peaks peaks;
    
    private List<Ms2FileScanCharge> chargeStates;
    
    private HashMap<String, String> analysisItems;
    
    public Ms2FileScan() {
        chargeStates = new ArrayList<Ms2FileScanCharge>();
        analysisItems = new HashMap<String, String>();
        peaks = new Peaks();
    }

    public void addPeak(float mz, double intensity) {
        peaks.addPeak(mz, intensity);
    }
    
    public void addAnalysisItem(String label, String value) {
        if (label == null || value == null)   return;
        analysisItems.put(label, value);
    }
    
    public String getValueForAnalysisLavel(String label) {
        return analysisItems.get(label);
    }
    
    public float getRetentionTime() {
        String rtStr = getValueForAnalysisLavel("RetTime");
        if (rtStr == null)
            return -1;
        else
            try { return Float.parseFloat(rtStr);}
            catch (NumberFormatException e) {return -1;}
    }
    
    public String getActivationType() {
        return getValueForAnalysisLavel("ActivationType");
    }
    
    public int getPrecursorScanNumber() {
        String scanNumStr = getValueForAnalysisLavel("PrecursorScan");
        if (scanNumStr == null)
            return -1;
        else
            try { return Integer.parseInt(scanNumStr);}
            catch (NumberFormatException e) {return -1;}
    }
    
    /**
     * @return the chargeStates
     */
    public List<Ms2FileScanCharge> getChargeStates() {
        return chargeStates;
    }


    /**
     * @param chargeState the chargeState to add
     */
    public void addChargeState(Ms2FileScanCharge chargeState) {
        chargeStates.add(chargeState);
    }


    /**
     * @return the startScan
     */
    public int getStartScan() {
        return startScan;
    }

    /**
     * @param startScan the startScan to set
     */
    public void setStartScan(int startScan) {
        this.startScan = startScan;
    }

    /**
     * @return the endScan
     */
    public int getEndScan() {
        return endScan;
    }

    /**
     * @param endScan the endScan to set
     */
    public void setEndScan(int endScan) {
        this.endScan = endScan;
    }

    /**
     * @return the precursorMz
     */
    public float getPrecursorMz() {
        return precursorMz;
    }

    /**
     * @param precursorMz the precursorMz to set
     */
    public void setPrecursorMz(float precursorMz) {
        this.precursorMz = precursorMz;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("S\t");
        buf.append(startScan);
        buf.append("\t");
        buf.append(endScan);
        buf.append("\t");
        buf.append(precursorMz);
        buf.append("\n");
        // charge independent analysis
        for (String label: analysisItems.keySet()) {
            buf.append("I\t");
            buf.append(label);
            buf.append("\t");
            buf.append(analysisItems.get(label));
            buf.append("\n");
        }
        // charge states along with their charge dependent analysis
        for (Ms2FileScanCharge charge: chargeStates) {
            buf.append(charge.toString());
            buf.append("\n");
        }
        // peak data
        Iterator<Peak> peakIterator = peaks.getIterator();
        while (peakIterator.hasNext()) {
            Peak peak = peakIterator.next();
            buf.append(peak.getMz());
            buf.append(" ");
            buf.append(peak.getIntensity());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }
}
