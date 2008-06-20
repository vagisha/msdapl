/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.dto.Peak;
import org.yeastrc.ms.dto.Peaks;

/**
 * 
 */
public class Scan {

    public static final String PRECURSOR_SCAN = "PrecursorScan";
    public static final String ACTIVATION_TYPE = "ActivationType";
    public static final String RET_TIME = "RetTime";
    
    private int startScan = -1;
    private int endScan = -1;
    
    private float precursorMz = -1;
    
    private Peaks peaks;
    
    private List<ScanCharge> chargeStates;
    
    private HashMap<String, String> analysisItems;
    
    public Scan() {
        chargeStates = new ArrayList<ScanCharge>();
        analysisItems = new HashMap<String, String>();
        peaks = new Peaks();
    }

    public void addPeak(float mz, double intensity) {
        peaks.addPeak(mz, intensity);
    }
    
    public byte[] getPeaksBinary() {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(peaks);
            oos.flush();
            return baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (oos != null) {
                try {oos.close();}
                catch (IOException e) {e.printStackTrace();}
            }
        }
        return null;
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
    
    public float getRetentionTime() {
        String rtStr = getValueForAnalysisLabel(RET_TIME);
        if (rtStr == null)
            return -1;
        else
            try { return Float.parseFloat(rtStr);}
            catch (NumberFormatException e) {return -1;}
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
        for (ScanCharge charge: chargeStates) {
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
