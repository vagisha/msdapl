/**
 * SQTSpectrumDataDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;

/**
 * 
 */
public class SQTSearchScanSqlMapParam implements SQTSearchScan {

    private int searchId;
    private int scanId;
    private SQTSearchScan scan;
    
    public SQTSearchScanSqlMapParam(int searchId, int scanId, SQTSearchScan scan) {
        this.searchId = searchId;
        this.scanId = scanId;
        this.scan = scan;
    }

    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }

    public int getCharge() {
        return scan.getCharge();
    }

    public BigDecimal getLowestSp() {
        return scan.getLowestSp();
    }

    public int getProcessTime() {
        return scan.getProcessTime();
    }

    public String getServerName() {
        return scan.getServerName();
    }

    public BigDecimal getTotalIntensity() {
        return scan.getTotalIntensity();
    }
    
}
