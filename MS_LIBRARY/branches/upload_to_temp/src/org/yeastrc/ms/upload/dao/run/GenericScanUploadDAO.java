/**
 * GenericScanDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run;

import java.util.List;

import org.yeastrc.ms.domain.run.MsScanIn;

/**
 * 
 */
public interface GenericScanUploadDAO <I extends MsScanIn> {

    /**
     * Saves the given scan in the database.
     * @param scan
     * @return
     */
    public abstract int save(I scan, int runId, int precursorScanId);
    
    /**
     * Saves the given scan in the database.
     * This method should be used when there is no precursorScanId for the scan
     * e.g. a MS1 scan
     * @param scan
     * @return
     */
    public abstract int save(I scan, int runId);

    public abstract <T extends MsScanIn> List<Integer> save(List<T> scans, int runId);
    
    
    public abstract void delete(int scanId);
    
    /**
     * Returns the database id for the scan with the given scan number and runId
     * @param scanNum
     * @param runId
     * @return
     */
    public abstract int loadScanIdForScanNumRun(int scanNum, int runId);
}
