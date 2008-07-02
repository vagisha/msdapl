/**
 * MS2FileScan.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.dto.IMsScan;
import org.yeastrc.ms.dto.Peaks;
import org.yeastrc.ms.dto.Peaks.Peak;

/**
 * 
 */
public class MS2FileScan implements IMsScan {

   
    private List<MS2FileScanCharge> scanChargeList;
    private List<MS2FileChargeIndependentAnalysis> chargeIndependentAnalysisList;
    private IMsScan scan;
    
    public MS2FileScan(IMsScan scan) {
        this.scan = scan;
        scanChargeList = new ArrayList<MS2FileScanCharge>();
        chargeIndependentAnalysisList = new ArrayList<MS2FileChargeIndependentAnalysis>();
    }
    
    
    @Override
    public int getEndScanNum() {
        return scan.getEndScanNum();
    }

    
    @Override
    public String getFragmentationType() {
       return scan.getFragmentationType();
    }

   
    @Override
    public int getId() {
        return scan.getId();
    }

    
    @Override
    public int getMsLevel() {
        return scan.getMsLevel();
    }

    
    @Override
    public Peaks getPeaks() {
        return scan.getPeaks();
    }

    
    @Override
    public byte[] getPeaksBinary() {
        return scan.getPeaksBinary();
    }

    
    @Override
    public Iterator<Peak> getPeaksIterator() {
       return scan.getPeaksIterator();
    }

   
    @Override
    public BigDecimal getPrecursorMz() {
       return scan.getPrecursorMz();
    }

    
    @Override
    public int getPrecursorScanId() {
       return scan.getPrecursorScanId();
    }

    
    @Override
    public int getPrecursorScanNum() {
       return scan.getPrecursorScanNum();
    }

    
    @Override
    public BigDecimal getRetentionTime() {
       return scan.getRetentionTime();
    }

   
    @Override
    public int getRunId() {
        return scan.getRunId();
    }

    
    @Override
    public int getStartScanNum() {
        return scan.getStartScanNum();
    }

   
    @Override
    public void setEndScanNum(int endScanNum) {
        scan.setEndScanNum(endScanNum);
    }

   
    @Override
    public void setFragmentationType(String fragmentationType) {
       scan.setFragmentationType(fragmentationType);
    }

    
    @Override
    public void setId(int id) {
       scan.setId(id);
    }

    
    @Override
    public void setMsLevel(int msLevel) {
       scan.setMsLevel(msLevel);
    }

   
    @Override
    public void setPeaks(Peaks peaks) {
       scan.setPeaks(peaks);
    }

   
    @Override
    public void setPeaksBinary(byte[] peakData) throws Exception {
       scan.setPeaksBinary(peakData);
    }

   
    @Override
    public void setPrecursorMz(BigDecimal precursorMz) {
        scan.setPrecursorMz(precursorMz);
    }

    
    @Override
    public void setPrecursorScanId(int precursorScanId) {
       scan.setPrecursorScanId(precursorScanId);
    }

   
    @Override
    public void setPrecursorScanNum(int precursorScanNum) {
       scan.setPrecursorScanNum(precursorScanNum);
    }

    
    @Override
    public void setRetentionTime(BigDecimal retentionTime) {
       scan.setRetentionTime(retentionTime);
    }

   
    @Override
    public void setRunId(int runId) {
       scan.setRunId(runId);
    }

   
    @Override
    public void setStartScanNum(int startScanNum) {
       scan.setStartScanNum(startScanNum);
    }


    /**
     * @return the scanChargeList
     */
    public List<MS2FileScanCharge> getScanChargeList() {
        return scanChargeList;
    }


    /**
     * @param scanChargeList the scanChargeList to set
     */
    public void setScanChargeList(List<MS2FileScanCharge> scanChargeList) {
        this.scanChargeList = scanChargeList;
    }


    /**
     * @return the chargeIndependentAnalysisList
     */
    public List<MS2FileChargeIndependentAnalysis> getChargeIndependentAnalysisList() {
        return chargeIndependentAnalysisList;
    }


    /**
     * @param chargeIndependentAnalysisList the chargeIndependentAnalysisList to set
     */
    public void setChargeIndependentAnalysisList(
            List<MS2FileChargeIndependentAnalysis> chargeIndependentAnalysisList) {
        this.chargeIndependentAnalysisList = chargeIndependentAnalysisList;
    }

}
