/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MsScan {

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
    
    private List <BigDecimal[]> peaks;
    
    private List <MsScanCharge> scanCharges; // charge states for this scan
    
    public MsScan() {
        peaks = new ArrayList<BigDecimal[]>();
        scanCharges = new ArrayList<MsScanCharge>();
    }

    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }

    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the startScanNum
     */
    public int getStartScanNum() {
        return startScanNum;
    }

    /**
     * @param startScanNum the startScanNum to set
     */
    public void setStartScanNum(int startScanNum) {
        this.startScanNum = startScanNum;
    }

    /**
     * @return the endScanNum
     */
    public int getEndScanNum() {
        return endScanNum;
    }

    /**
     * @param endScanNum the endScanNum to set
     */
    public void setEndScanNum(int endScanNum) {
        this.endScanNum = endScanNum;
    }

    /**
     * @return the msLevel
     */
    public int getMsLevel() {
        return msLevel;
    }

    /**
     * @param msLevel the msLevel to set
     */
    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    /**
     * @return the fragmentationType
     */
    public String getFragmentationType() {
        return fragmentationType;
    }

    /**
     * @param fragmentationType the fragmentationType to set
     */
    public void setFragmentationType(String fragmentationType) {
        this.fragmentationType = fragmentationType;
    }
    
    /**
     * @return the retentionTime
     */
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    
    /**
     * @param retentionTime the retentionTime to set
     */
    public void setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
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

    /**
     * @return the precursorScanId
     */
    public int getPrecursorScanId() {
        return precursorScanId;
    }

    /**
     * @param precursorScanId the precursorScanId to set
     */
    public void setPrecursorScanId(int precursorScanId) {
        this.precursorScanId = precursorScanId;
    }

    /**
     * @param peaks the peaks to set
     */
    public void setPeaks(List<BigDecimal[]> peaks) {
        this.peaks = peaks;
    }

    public List<BigDecimal[]> getPeaks() {
        return peaks;
    }
    
    public void setPeaksBinary(byte[] peakData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(peakData);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            peaks = (List<BigDecimal[]>) ois.readObject();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (ois != null) {
                try {ois.close();} 
                catch (IOException e) {e.printStackTrace();}
            }
        }
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
    
    /**
     * @return the scanCharges
     */
    public List<MsScanCharge> getScanCharges() {
        return scanCharges;
    }

    /**
     * @param scanCharges the scanCharges to set
     */
    public void setScanCharges(List<MsScanCharge> scanCharges) {
        this.scanCharges = scanCharges;
    }

    /**
     * @return the precursorScanNum
     */
    public int getPrecursorScanNum() {
        return precursorScanNum;
    }

    /**
     * @param precursorScanNum the precursorScanNum to set
     */
    public void setPrecursorScanNum(int precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }
    
    
    
}
