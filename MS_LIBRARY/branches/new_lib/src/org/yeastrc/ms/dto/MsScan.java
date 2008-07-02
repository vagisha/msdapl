/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.util.Iterator;

import org.yeastrc.ms.dto.Peaks.Peak;

/**
 * 
 */
public class MsScan implements IMsScan {

    private int runId;  // id (database) of the run this scan belongs to
    
    private int id;     // unique id (database) of this scan
    
    private int startScanNum = -1;
    private int endScanNum = -1;
    
    private int msLevel = -1; // 1 for MS1, 2 for MS2 and so on
    private BigDecimal retentionTime;
    private String fragmentationType; 
    
    private BigDecimal precursorMz;  // 0 if this is a MS1 scan
    private int precursorScanId = -1; // id (database) of the precursor scan.  0 if this is a MS1 scan
    private int precursorScanNum = -1; // scan number of the precursor scan
    
    private Peaks peaks;
    
    public MsScan() {
        peaks = new Peaks();
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getRunId()
     */
    public int getRunId() {
        return runId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setRunId(int)
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getId()
     */
    public int getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setId(int)
     */
    public void setId(int id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getStartScanNum()
     */
    public int getStartScanNum() {
        return startScanNum;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setStartScanNum(int)
     */
    public void setStartScanNum(int startScanNum) {
        this.startScanNum = startScanNum;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getEndScanNum()
     */
    public int getEndScanNum() {
        return endScanNum;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setEndScanNum(int)
     */
    public void setEndScanNum(int endScanNum) {
        this.endScanNum = endScanNum;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getMsLevel()
     */
    public int getMsLevel() {
        return msLevel;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setMsLevel(int)
     */
    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getFragmentationType()
     */
    public String getFragmentationType() {
        return fragmentationType;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setFragmentationType(java.lang.String)
     */
    public void setFragmentationType(String fragmentationType) {
        this.fragmentationType = fragmentationType;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getRetentionTime()
     */
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setRetentionTime(java.math.BigDecimal)
     */
    public void setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPrecursorMz()
     */
    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setPrecursorMz(java.math.BigDecimal)
     */
    public void setPrecursorMz(BigDecimal precursorMz) {
        this.precursorMz = precursorMz;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPrecursorScanId()
     */
    public int getPrecursorScanId() {
        return precursorScanId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setPrecursorScanId(int)
     */
    public void setPrecursorScanId(int precursorScanId) {
        this.precursorScanId = precursorScanId;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setPeaks(org.yeastrc.ms.dto.Peaks)
     */
    public void setPeaks(Peaks peaks) {
        this.peaks = peaks;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPeaks()
     */
    public Peaks getPeaks() {
        return peaks;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setPeaksBinary(byte[])
     */
    public void setPeaksBinary(byte[] peakData) throws Exception {
        peaks.setPeakDataBinary(peakData);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPeaksBinary()
     */
    public byte[] getPeaksBinary() {
        return peaks.getPeakDataBinary();
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPeaksIterator()
     */
    public Iterator<Peak> getPeaksIterator() {
        return peaks.iterator();
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#getPrecursorScanNum()
     */
    public int getPrecursorScanNum() {
        return precursorScanNum;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsScan#setPrecursorScanNum(int)
     */
    public void setPrecursorScanNum(int precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }
    
    
    
}
