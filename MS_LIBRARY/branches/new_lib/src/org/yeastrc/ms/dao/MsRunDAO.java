package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsRun;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;

public interface MsRunDAO {

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any file-format specific information in the run is also saved.
     * @param run
     * @return
     */
    public abstract int saveRun(IMsRun run);
    
    
    /**
     * Returns a run from the database with the given runId
     * @param runId
     * @return
     */
    public abstract IMsRun loadRun(int runId);
    
    /**
     * Returns a run from the database, along with any file-format specific information 
     * associated with the run. 
     * @param runId
     * @return
     */
    public abstract IMsRun loadRunForFormat(int runId);
    
    
    /**
     * Returns the list of runs for the given experiment ID.
     * The returned runs have associated enzyme related information, but NOT any
     * file-format specific information.
     * @param msExperimentId
     * @return
     */
    public abstract List<IMsRun> loadExperimentRuns(int msExperimentId);
    
    
    /**
     * Returns the list of run IDs for the given experiment ID.
     * @param msExperimentId
     * @return
     */
    public abstract List<Integer> loadRunIdsForExperiment(int msExperimentId);
    
    
    /**
     * Returns a list of run IDs for runs in the database with the given file name
     * and sha1sum. 
     * @param fileName
     * @param sha1Sum
     * @return
     */
    public abstract List<Integer> runIdsFor(String fileName, String sha1Sum);
    
    
    /**
     * This will delete all the runs associated with this experiment, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs.
     * File-format specific information associated with the runs is also deleted. 
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public abstract List<Integer> deleteRunsForExperiment(int msExperimentId);
    
    
    /**
     * Returns the original file format for the run.
     * @param runId
     * @return
     * @throws RuntimeException if a run with the given id is not found in the database
     */
    public abstract RunFileFormat getRunFileFormat(int runId);

}