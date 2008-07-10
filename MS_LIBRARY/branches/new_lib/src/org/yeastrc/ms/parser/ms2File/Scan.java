/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.domain.IPeaks;
import org.yeastrc.ms.domain.db.Peaks;
import org.yeastrc.ms.domain.db.Peaks.Peak;

/**
 * 
 */
public class Scan {

    public static final String PRECURSOR_SCAN = "PrecursorScan";
    public static final String ACTIVATION_TYPE = "ActivationType";
    public static final String RET_TIME = "RetTime";
    
    private int startScan = -1;
    private int endScan = -1;
    
    private BigDecimal precursorMz;
    
    private Peaks peaks;
    
    private List<ScanCharge> chargeStates;
    
    private HashMap<String, String> analysisItems;
    
    public Scan() {
        chargeStates = new ArrayList<ScanCharge>();
        analysisItems = new HashMap<String, String>();
        peaks = new Peaks();
    }

    public void setPeaks(Peaks peaks) {
        this.peaks = peaks;
    }
    
    public IPeaks getPeaks() {
        return peaks;
    }
    
    public Iterator<Peak> getPeaksIterator() {
        return peaks.iterator();
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
    
    public BigDecimal getRetentionTime() {
        String rtStr = getValueForAnalysisLabel(RET_TIME);
        if (rtStr != null)
            return new BigDecimal(rtStr);
        else
            return null;
    }
    
    public String getActivationType() {
        return getValueForAnalysisLabel(ACTIVATION_TYPE);
    }
    
    public int getPrecursorScanNumber() {
        String scanNumStr = getValueForAnalysisLabel(PRECURSOR_SCAN);
        if (scanNumStr == null)
            return -1;
        else
            try { return Integer.parseInt(scanNumStr);}
            catch (NumberFormatException e) {return -1;}
    }
    
    /**
     * @return the chargeStates
     */
    public List<ScanCharge> getChargeStates() {
        return chargeStates;
    }


    /**
     * @param chargeState the chargeState to add
     */
    public void addChargeState(ScanCharge chargeState) {
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
    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }

    /**
     * @param precursorMz the precursorMz to set
     */
    public void setPrecursorMz(BigDecimal precursorMz) {
        this.precursorMz = precursorMz;
    }
    
    public void setPrecursorMz(String precursorMz) {
        this.precursorMz = new BigDecimal(precursorMz);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("S\t");
        buf.append(String.format("%06d", startScan));
        buf.append("\t");
        buf.append(String.format("%06d", endScan));
        if (precursorMz != null) {
            buf.append("\t");
            buf.append(precursorMz);
        }
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
        for (ScanCharge charge: chargeStates) {
            buf.append(charge.toString());
            buf.append("\n");
        }
        // peak data
        Iterator<Peak> iterator = getPeaksIterator();
        while (iterator.hasNext()) {
            Peak peak = iterator.next();
            buf.append(peak.getMzString());
            buf.append(" ");
            buf.append(peak.getIntensityString());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }
}
