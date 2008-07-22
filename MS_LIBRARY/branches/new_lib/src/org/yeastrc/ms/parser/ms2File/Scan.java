/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;

/**
 * 
 */
public class Scan implements MS2Scan {

    public static final String PRECURSOR_SCAN = "PrecursorScan"; // precursor scan number
    public static final String ACTIVATION_TYPE = "ActivationType";
    public static final String RET_TIME = "RetTime";
    
    private int startScan = -1;
    private int endScan = -1;
    private int precursorScanNum = -1;
    
    private BigDecimal precursorMz;
    private BigDecimal retentionTime;
    private String activationType;
    
    private List<String[]> peakList;
    
    private List<MS2ScanCharge> chargeStates;
    
    private List<MS2Field> analysisItems;
    
    
    public Scan() {
        chargeStates = new ArrayList<MS2ScanCharge>();
        analysisItems = new ArrayList<MS2Field>();
        peakList = new ArrayList<String[]>();
    }

    public boolean isValid() {
        return peakList.size() > 0;
    }
    
    @Override
    public List<MS2Field> getChargeIndependentAnalysisList() {
        return this.analysisItems;
    }
    
    /**
     * @throws DataFormatException is thrown if we are parsing the "ActivationType" header
     *         and the value is more than 3 characters.
     */
    public void addAnalysisItem(String label, String value) throws DataFormatException {
        if (label == null || value == null)   return;
        analysisItems.add(new HeaderItem(label, value));
        if (label.equalsIgnoreCase(RET_TIME))
            setRetentionTime(value);
        else if (label.equalsIgnoreCase(PRECURSOR_SCAN))
            setPrecursorScanNum(value);
        else if (label.equalsIgnoreCase(ACTIVATION_TYPE))
            setFragmentationType(value);
    }
    
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    private void setRetentionTime(String rt) {
        this.retentionTime = new BigDecimal(rt);
    }
    
    public int getPrecursorScanNum() {
       return precursorScanNum;
    }
    private void setPrecursorScanNum(String num) {
        this.precursorScanNum = Integer.parseInt(num);
    }
    
    public List<MS2ScanCharge> getScanChargeList() {
        return this.chargeStates;
    }
    public void addChargeState(ScanCharge chargeState) {
        chargeStates.add(chargeState);
    }

    public int getStartScanNum() {
        return startScan;
    }
    public void setStartScan(int startScan) {
        this.startScan = startScan;
    }

    public int getEndScanNum() {
        return this.endScan;
    }
    public void setEndScan(int endScan) {
        this.endScan = endScan;
    }

    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }
    public void setPrecursorMz(String precursorMz) {
        this.precursorMz = new BigDecimal(precursorMz);
    }
 

    public Iterator<String[]> peakIterator() {
        return peakList.iterator();
    }
    public void addPeak(String mz, String rt) {
        peakList.add(new String[]{mz, rt});
    }
    
    public String getFragmentationType() {
        return activationType;
    }
    /**
     * @throws DataFormatException is thrown if actType is more than 3 characters.
     * 
     * The database (msScan table) currently supports a 3 character value for 
     * fragmentationType.
     */
    private void setFragmentationType(String actType) throws DataFormatException {
        this.activationType = actType;
        if (actType.length() > 3)
            throw new DataFormatException("Invalid fragmentation type (> 3 characters): "+actType);
    }

    public int getMsLevel() {
        return 2;
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
        for (MS2Field item: analysisItems) {
            buf.append("I\t");
            buf.append(item.getName());
            buf.append("\t");
            buf.append(item.getValue());
            buf.append("\n");
        }
        // charge states along with their charge dependent analysis
        for (MS2ScanCharge charge: chargeStates) {
            buf.append(charge.toString());
            buf.append("\n");
        }
        // peak data
        for (String[] peak: peakList){
            buf.append(peak[0]);
            buf.append(" ");
            buf.append(peak[1]);
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }
}
