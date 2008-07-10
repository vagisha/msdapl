/**
 * MS2FileScan.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.db.MsScan;

/**
 * 
 */
public class MS2FileScan extends MsScan {

   
    private List<MS2FileScanCharge> scanChargeList;
    private List<MS2FileChargeIndependentAnalysis> chargeIndependentAnalysisList;
    
    public MS2FileScan() {
        scanChargeList = new ArrayList<MS2FileScanCharge>();
        chargeIndependentAnalysisList = new ArrayList<MS2FileChargeIndependentAnalysis>();
    }
    
    public MS2FileScan(MsScan scan) {
        this();
        setRunId(scan.getRunId());
        setStartScanNum(scan.getStartScanNum());
        setEndScanNum(scan.getEndScanNum());
        setMsLevel(scan.getMsLevel());
        setPrecursorMz(scan.getPrecursorMz());
        setPrecursorScanId(scan.getPrecursorScanId());
        setPrecursorScanNum(scan.getPrecursorScanNum());
        setRetentionTime(scan.getRetentionTime());
        setFragmentationType(scan.getFragmentationType());
        setPeaks(scan.getPeaks());
    }
    
    /**
     * @return the scanChargeList
     */
    public List<MS2FileScanCharge> getScanChargeList() {
        return scanChargeList;
    }

    public void addScanCharge(MS2FileScanCharge scanCharge) {
        scanChargeList.add(scanCharge);
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

    public void addChargeIndependentAnalysis(MS2FileChargeIndependentAnalysis analysis) {
        chargeIndependentAnalysisList.add(analysis);
    }
    
    /**
     * @param chargeIndependentAnalysisList the chargeIndependentAnalysisList to set
     */
    public void setChargeIndependentAnalysisList(
            List<MS2FileChargeIndependentAnalysis> chargeIndependentAnalysisList) {
        this.chargeIndependentAnalysisList = chargeIndependentAnalysisList;
    }

}
