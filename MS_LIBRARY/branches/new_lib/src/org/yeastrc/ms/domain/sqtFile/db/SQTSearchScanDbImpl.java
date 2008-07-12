/**
 * SQTSpectrumData.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.sqtFile.db;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.sqtFile.SQTSearchScanDb;

/**
 * 
 */
public class SQTSearchScanDbImpl implements SQTSearchScanDb {

    private int scanId;
    private int charge;
    private int searchId;
    private int processTime;
    private String serverName;
    private BigDecimal totalIntensity;
    private BigDecimal lowestSp;
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
    /**
     * @param scanId the scanId to set
     */
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }
    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }
    /**
     * @param searchId the searchId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
    /**
     * @return the processTime
     */
    public int getProcessTime() {
        return processTime;
    }
    /**
     * @param processTime the processTime to set
     */
    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }
    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }
    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     * @return the totalIntensity
     */
    public BigDecimal getTotalIntensity() {
        return totalIntensity;
    }
    /**
     * @param totalIntensity the totalIntensity to set
     */
    public void setTotalIntensity(BigDecimal totalIntensity) {
        this.totalIntensity = totalIntensity;
    }
    /**
     * @return the lowestSp
     */
    public BigDecimal getLowestSp() {
        return lowestSp;
    }
    /**
     * @param lowestSp the lowestSp to set
     */
    public void setLowestSp(BigDecimal lowestSp) {
        this.lowestSp = lowestSp;
    }
}
