package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsRun;

public interface MsRunDAO {

    public abstract int save(MsRun run);

    public abstract MsRun load(int runId);
    
    public abstract List<MsRun> loadRunsForExperiment(int msExperimentId);

    public abstract void update(MsRun run);

}