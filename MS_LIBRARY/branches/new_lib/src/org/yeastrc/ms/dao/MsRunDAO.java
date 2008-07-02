package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsRunWithEnzymeInfo;

public interface MsRunDAO {

    public abstract int saveRun(MsRun run);
    
    public abstract int saveRunWithEnzymeInfo(MsRunWithEnzymeInfo run);
    

    public abstract MsRun loadRun(int runId);
    
    public abstract MsRunWithEnzymeInfo loadRunWithEmzymeInfo(int runId);
    
    
    public abstract List<MsRun> loadRuns(String fileName, String sha1Sum);
    
    public abstract List<MsRunWithEnzymeInfo> loadRunsWithEnzymeInfo(String fileName, String sha1Sum);
    

    public abstract List<MsRun> loadExperimentRuns(int msExperimentId);
    
    public abstract List<MsRunWithEnzymeInfo> loadExperimentRunsWithEnzymeInfo(int msExperimentId);
    
    
    public abstract List<Integer> loadRunIdsForExperiment(int msExperimentId);
    
    /**
     * This will delete all the runs associated with this experiment, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs.
     * @param msExperimentId
     */
    public abstract void deleteRunsForExperiment(int msExperimentId);

}