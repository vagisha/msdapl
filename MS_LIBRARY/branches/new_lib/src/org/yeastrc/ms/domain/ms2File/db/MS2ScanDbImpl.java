/**
 * MS2FileScan.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File.db;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.db.MsScanDbImpl;
import org.yeastrc.ms.domain.ms2File.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2ScanChargeDb;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;

/**
 * 
 */
public class MS2ScanDbImpl extends MsScanDbImpl implements MS2ScanDb {

   
    private List<? super MS2ScanChargeDb> scanChargeList;
    private List<? super MS2ChargeIndependentAnalysisDb> chargeIndependentAnalysisList;
    
    public MS2ScanDbImpl() {
        scanChargeList = new ArrayList<MS2ScanChargeDb>();
        chargeIndependentAnalysisList = new ArrayList<MS2ChargeIndependentAnalysisDb>();
    }
    
    /**
     * @return the scanChargeList
     */
    public List<MS2ScanChargeDb> getScanChargeList() {
        return (List<MS2ScanChargeDb>) scanChargeList;
    }

    public void addScanCharge(MS2ScanChargeDbImpl scanCharge) {
        scanChargeList.add(scanCharge);
    }
    
    /**
     * @param scanChargeList the scanChargeList to set
     */
    public void setScanChargeList(List<? super MS2ScanChargeDb> scanChargeList) {
        this.scanChargeList = scanChargeList;
    }

    /**
     * @return the chargeIndependentAnalysisList
     */
    public List<MS2ChargeIndependentAnalysisDb> getChargeIndependentAnalysisList() {
        return (List<MS2ChargeIndependentAnalysisDb>) chargeIndependentAnalysisList;
    }

    public void addChargeIndependentAnalysis(MS2ChargeIndependentAnalysisDbImpl analysis) {
        chargeIndependentAnalysisList.add(analysis);
    }
    
    /**
     * @param chargeIndependentAnalysisList the chargeIndependentAnalysisList to set
     */
    public void setChargeIndependentAnalysisList(List<? super MS2ChargeIndependentAnalysisDb> chargeIndependentAnalysisList) {
        this.chargeIndependentAnalysisList = chargeIndependentAnalysisList;
    }

}
