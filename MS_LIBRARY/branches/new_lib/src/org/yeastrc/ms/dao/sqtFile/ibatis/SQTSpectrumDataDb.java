/**
 * SQTSpectrumDataDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import org.yeastrc.ms.domain.sqtFile.ISQTSearchScan;

/**
 * 
 */
public class SQTSpectrumDataDb {

    private int searchId;
    private int scanId;
    private ISQTSearchScan scan;
    
    public SQTSpectrumDataDb(int searchId, int scanId, ISQTSearchScan scan) {
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

    /**
     * @return the scan
     */
    public ISQTSearchScan getScan() {
        return scan;
    }
}
