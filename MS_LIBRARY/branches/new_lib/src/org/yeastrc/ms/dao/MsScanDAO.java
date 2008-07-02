package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsScan;

public interface MsScanDAO {

    public abstract int save(MsScan scan);

    public abstract MsScan load(int scanId);
    
    public abstract List<Integer> loadScanIdsForRun(int runId);
    
    public abstract void deleteScansForRun(int runId);
    
    public abstract void deleteScansForRuns(List<Integer> runIds);

}