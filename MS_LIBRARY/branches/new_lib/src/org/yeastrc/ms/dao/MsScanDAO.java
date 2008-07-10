package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.IMsScan;
import org.yeastrc.ms.domain.db.MsScan;

public interface MsScanDAO <I extends IMsScan, O extends MsScan> {

    /**
     * Saves the given scan in the database.
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
    
}