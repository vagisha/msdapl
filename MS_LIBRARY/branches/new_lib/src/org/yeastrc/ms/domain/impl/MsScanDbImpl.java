/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.util.PeakUtils;

/**
 * 
 */
public class MsScanDbImpl implements MsScanDb {

    private int runId;  // id (database) of the run this scan belongs to
    
    private int id;     // unique id (database) of this scan
    
    private int startScanNum = -1;
    private int endScanNum = -1;
    
    private int msLevel = 0; // 1 for MS1, 2 for MS2 and so on
    private BigDecimal retentionTime;
    private String fragmentationType; 
    
    private BigDecimal precursorMz;  // 0 if this is a MS1 scan
    private int precursorScanId = 0; // id (database) of the precursor scan.  0 if this is a MS1 scan
    private int precursorScanNum = -1; // scan number of the precursor scan
    
   // private List<double[]> peaks;
    
    private String peakString;
    
    public MsScanDbImpl() {
        //peaks = new ArrayList<double[]>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getStartScanNum() {
        return startScanNum;
    }

    public void setStartScanNum(int startScanNum) {
        this.startScanNum = startScanNum;
    }

    public int getEndScanNum() {
        return endScanNum;
    }

    public void setEndScanNum(int endScanNum) {
        this.endScanNum = endScanNum;
    }

    public int getPrecursorScanNum() {
        return precursorScanNum;
    }

    public void setPrecursorScanNum(int precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }
    
    public int getPrecursorScanId() {
        return precursorScanId;
    }

    public void setPrecursorScanId(int precursorScanId) {
        this.precursorScanId = precursorScanId;
    }
    
    public int getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public String getFragmentationType() {
        return fragmentationType;
    }

    public void setFragmentationType(String fragmentationType) {
        this.fragmentationType = fragmentationType;
    }
    
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    
    public void setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
    }
    
    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }

    public void setPrecursorMz(BigDecimal precursorMz) {
        this.precursorMz = precursorMz;
    }


//    public Iterator<double[]> peakIterator() {
//        return peaks.iterator();
//    }
    
    public void setPeakByteArray(byte[] peaks) throws IOException, ClassNotFoundException {
        peakString = PeakUtils.decodePeakString(peaks);
//        parsePeaksAsString(peakString);
    }

    @Override
    public String peakDataString() {
        return peakString;
    }
    
//    void parsePeaksAsString(String peaksString) {
//        if (peaksString == null || peaksString.length() == 0)
//            return;
//        String[] peaksStr = peaksString.split(";");
//        for (String peak: peaksStr) {
//            String [] peakVals = splitPeakVals(peak);
//            double[] peakData = new double[2];
//            peakData[0] = Double.parseDouble(peakVals[0]);
//            peakData[1] = Double.parseDouble(peakVals[1]);
//            peaks.add(peakData);
//        }
//    }
    
//    private String[] splitPeakVals(String peak) {
//        int i = peak.indexOf(":");
//        String[] vals = new String[2];
//        vals[0] = peak.substring(0, i);
//        vals[1] = peak.substring(i+1, peak.length());
//        return vals;
//    }
}
