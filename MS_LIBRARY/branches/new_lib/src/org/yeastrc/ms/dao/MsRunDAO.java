package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.RunFileFormat;

public interface MsRunDAO <I extends MsRun, O extends MsRunDb>{

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any enzyme information is saved
     * An entry is creatd in msExperimentRun linking this run to the experiment.
     * @param run
     * @return
     */
    public abstract int saveRun(I run, int experimentId);
    
    
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
     * Returns the runId for the given experiment and file name. Returns
     * 0 if no matching run was found
     * @param experimentId
     * @param fileName
     * @return
     */
    public abstract int loadRunIdForExperimentAndFileName(int experimentId, String fileName);
    
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
     * Returns the original file format for the run.
     * @param runId
     * @return
     * @throws Exception if a run with the given id is not found in the database
     */
    public abstract RunFileFormat getRunFileFormat(int runId) throws Exception;

}