package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.MsScan;

public interface MsScanDAO {

    public abstract int save(MsScan run);

    public abstract MsScan load(int scanId);
    
    public abstract List<Integer> loadScanIdsForRun(int runId);

    public abstract void update(MsScan run);

}