/**
 * MS2FileScan.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.impl.ScanBean;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;

/**
 * 
 */
public class MS2ScanBean extends ScanBean implements MS2Scan {

   
    private List<MS2ScanCharge> scanChargeList;
    private List<MS2NameValuePair> chargeIndependentAnalysisList;
    
    public MS2ScanBean() {
        scanChargeList = new ArrayList<MS2ScanCharge>();
        chargeIndependentAnalysisList = new ArrayList<MS2NameValuePair>();
    }
    
    public List<MS2ScanCharge> getScanChargeList() {
        return scanChargeList;
    }

    public void setScanChargeList(List<MS2ScanCharge> scanChargeList) {
        this.scanChargeList = scanChargeList;
    }

    public List<MS2NameValuePair> getChargeIndependentAnalysisList() {
        return chargeIndependentAnalysisList;
    }

    public void setChargeIndependentAnalysisList(List<MS2NameValuePair> chargeIndependentAnalysisList) {
        this.chargeIndependentAnalysisList = chargeIndependentAnalysisList;
    }

}
