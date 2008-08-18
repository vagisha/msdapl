package org.yeastrc.ms.dao.run;

import java.util.List;

import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;

public interface MsScanDAO <I extends MsScan, O extends MsScanDb> {

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

    /**
     * Returns a scan with the given scan ID from the database.
     * @param scanId
     * @return
     */
    public abstract O load(int scanId);
    
    
    /**
     * Returns a list of scan ID's for the given run
     * @param runId
     * @return
     */
    public abstract List<Integer> loadScanIdsForRun(int runId);
    
    
    public abstract void delete(int scanId);
    
    /**
     * Deletes all scans for the given run.
     * @param runId
     */
    public abstract void deleteScansForRun(int runId);

    /**
     * Returns the database id for the scan with the given scan number and runId
     * @param scanNum
     * @param runId
     * @return
     */
    public abstract int loadScanIdForScanNumRun(int scanNum, int runId);
    
}