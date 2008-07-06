package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsScan;

public interface MsScanDAO  {

    /**
     * Saves the given scan in the database.
     * @param scan
     * @return
     */
    public abstract int save(MsScan scan);

    /**
     * Returns a scan with the given scan ID from the database.
     * @param scanId
     * @return
     */
    public abstract MsScan load(int scanId);
    
    
    /**
     * Returns a list of scan ID's for the given run
     * @param runId
     * @return
     */
    public abstract List<Integer> loadScanIdsForRun(int runId);
    
    /**
     * Deletes all scans for the given run.
     * @param runId
     */
    public abstract void deleteScansForRun(int runId);
    
}