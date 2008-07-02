package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsScan;

public interface MsScanDAO {

    /**
     * Saves the given scan in the database. Any file-format specific information is saved as well.
     * @param scan
     * @return
     */
    public abstract int save(IMsScan scan);

    /**
     * Returns a scan with the given scan ID from the database.
     * File-format specific information is NOT part of the returned scan.
     * @param scanId
     * @return
     */
    public abstract IMsScan load(int scanId);
    
    /**
     * Returns a scan with the given scan ID from the database.
     * Any file-format specific information is also part of the returned scan.
     * @param scanId
     * @return
     */
    public abstract IMsScan loadForFormat(int scanId);
    
    /**
     * Returns a list of scan ID's for the given run
     * @param runId
     * @return
     */
    public abstract List<Integer> loadScanIdsForRun(int runId);
    
    /**
     * Deletes all scans (as well as any file-format specific information) for the 
     * given run.
     * @param runId
     */
    public abstract void deleteScansForRun(int runId);
    
    /**
     * Deletes all scans (as well as any file-format specific information) for the 
     * given list of run IDs.
     * @param runIds
     */
    public abstract void deleteScansForRuns(List<Integer> runIds);

}