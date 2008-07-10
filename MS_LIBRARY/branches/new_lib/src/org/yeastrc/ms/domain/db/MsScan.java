/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.db;

import java.math.BigDecimal;
import java.util.Iterator;

import org.yeastrc.ms.domain.IMsScan;
import org.yeastrc.ms.domain.IPeaks;
import org.yeastrc.ms.domain.db.Peaks.Peak;

/**
 * 
 */
public class MsScan implements IMsScan {

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
    
    private Peaks peaks;
    
    public MsScan() {
        peaks = new Peaks();
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPrecursorScanId() {
        return precursorScanId;
    }

    public void setPrecursorScanId(int precursorScanId) {
        this.precursorScanId = precursorScanId;
    }

    protected void setPeaks(Peaks peaks) {
        this.peaks = peaks;
    }

    public IPeaks getPeaks() {
        return peaks;
    }
    
    public void setPeaksBinary(byte[] peakData) throws Exception {
        peaks.setPeakDataBinary(peakData);
    }
    
    public int getPrecursorScanNum() {
        return precursorScanNum;
    }

    public void setPrecursorScanNum(int precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }
    
}
