package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.RunFileFormat;

public interface MsRunDAO <I extends MsRun, O extends MsRunDb>{

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any enzyme information is saved
     * @param run
     * @param msExperimentId 
     * @return
     */
    public abstract int saveRun(I run, int msExperimentId);
    
    
    /**
     * Returns a run from the database with the given runId
     * @param runId
     * @return
     */
    public abstract O loadRun(int runId);
    
    
    /**
     * Returns the list of runs for the given experiment ID.
     * The returned runs have associated enzyme related information
     * @param msExperimentId
     * @return
     */
    public abstract List<O> loadExperimentRuns(int msExperimentId);
    
    
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
     * Deletes the run with the given id. Enzyme information and scans are also deleted
     * @param runId
     */
    public abstract void delete(int runId);
    
    /**
     * This will delete all the runs associated with this experiment, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs as well as
     * the scans.
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public abstract List<Integer> deleteRunsForExperiment(int msExperimentId);
    
    
    /**
     * Returns the original file format for the run.
     * @param runId
     * @return
     * @throws Exception if a run with the given id is not found in the database
     */
    public abstract RunFileFormat getRunFileFormat(int runId) throws Exception;

}