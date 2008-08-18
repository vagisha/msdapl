package org.yeastrc.ms.dao.run;

import java.util.List;

import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.MsRunLocationDb;
import org.yeastrc.ms.domain.run.RunFileFormat;

public interface MsRunDAO <I extends MsRun, O extends MsRunDb>{

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any enzyme information is saved.
     * The location of the run is saved as well.
     * @param run
     * @param runLocation
     * @return
     */
    public abstract int saveRun(I run, String serverAddress, String serverDirectory);
    
    
    /**
     * Saves the original location of the run corresponding to the runId.
     * @param runLocation
     */
    public abstract void saveRunLocation(String serverAddress, String serverDirectory, int runId);
    
    
    /**
     * Returns a run from the database with the given runId
     * @param runId
     * @return
     */
    public abstract O loadRun(int runId);
    
    /**
     * Returns the list of runs for the given runIds
     * The returned runs have any associated enzyme related information as well.
     * @param runIdList
     * @return
     */
    public abstract List<O> loadRuns(List<Integer> runIdList);
    
    /**
     * Returns a list of run IDs for runs in the database with the given file name
     * and sha1sum. 
     * @param fileName
     * @param sha1Sum
     * @return
     */
    public abstract List<Integer> runIdsFor(String fileName, String sha1Sum);
    
    
    /**
     * Returns a list of locations for the given run
     * @param runId
     * @return
     */
    public abstract List<MsRunLocationDb> loadLocationsForRun(int runId);
    
    /**
     * Returns locations with the given runId, serverAddress and serverDirectory.
     * @param runId
     * @param serverAddress
     * @param serverDirectory
     * @return
     */
    public abstract List<MsRunLocationDb> loadMatchingRunLocations(int runId, String serverAddress, String serverDirectory);
    
    /**
     * Deletes the run with the given id. Enzyme information and scans are also deleted
     * Any location entries for this run are also deleted.
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