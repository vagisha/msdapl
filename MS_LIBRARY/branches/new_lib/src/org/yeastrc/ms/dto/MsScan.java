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
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MsScan {

    private int runId;  // id (database) of the run this scan belongs to
    
    private int id;     // unique id (database) of this scan
    
    private int startScanNum;
    private int endScanNum;
    
    private int msLevel; // 1 for MS1, 2 for MS2 and so on
    private double retentionTime;
    
    
    private double precursorMz;  // 0 if this is a MS1 scan
    private int precursorScanId; // id (database) of the precursor scan.  0 if this is a MS1 scan
    
    private Peaks peaks;
    
    private List <MsScanCharge> scanCharges; // charge states for this scan
    
    public MsScan() {
        peaks = new Peaks();
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
     * @return the retentionTime
     */
    public double getRetentionTime() {
        return retentionTime;
    }

    /**
     * @param retentionTime the retentionTime to set
     */
    public void setRetentionTime(double retentionTime) {
        this.retentionTime = retentionTime;
    }

    /**
     * @return the precursorMz
     */
    public double getPrecursorMz() {
        return precursorMz;
    }

    /**
     * @param precursorMz the precursorMz to set
     */
    public void setPrecursorMz(double precursorMz) {
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
     * @return the peaks
     */
    public Peaks getPeaks() {
        return peaks;
    }

    /**
     * @param peaks the peaks to set
     */
    public void setPeaks(Peaks peaks) {
        this.peaks = peaks;
    }

    public void setPeaksBinary(byte[] peakData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(peakData);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            peaks = (Peaks) ois.readObject();
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
    
    
    
}
