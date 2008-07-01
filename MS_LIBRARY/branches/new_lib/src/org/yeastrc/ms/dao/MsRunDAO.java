package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsRun;

public interface MsRunDAO {

    public abstract int save(MsRun run);

    public abstract MsRun load(int runId);
    
    public abstract List<Integer> loadRunIdsForExperiment(int msExperimentId);
    
    public abstract List<MsRun> loadRunsForExperiment(int msExperimentId);
    
    public abstract List<MsRun> loadRunsForFileNameAndSha1Sum(String fileName, String sha1Sum);

}