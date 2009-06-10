/**
 * GenericRunDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run;

import org.yeastrc.ms.domain.run.MsRunIn;

/**
 * 
 */
public interface GenericRunUploadDAO <I extends MsRunIn> {

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any enzyme information is saved.
     * The location of the run is saved as well.
     * @param run
     * @param runLocation
     * @return
     */
    public abstract int saveRun(I run, String serverDirectory);
    
    
    /**
     * Saves the original location of the run corresponding to the runId.
     * @param runLocation
     */
    public abstract void saveRunLocation(String serverDirectory, int runId);
    
    
    /**
     * Returns a list of run IDs for runs in the database with the given file name
     * and sha1sum. 
     * @param fileName
     * @param sha1Sum
     * @return
     */
    public abstract int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum);
    
    
    /**
     * Returns the runID for a run with the given file name that is associated with
     * the given experimentId.
     * @param experimentId
     * @param runFileName
     * @return
     */
    public abstract Integer loadRunIdForExperimentAndFileName(int experimentId, String runFileName);
    
    
    /**
     * Returns the runID for a run with the given file name that was searched in a
     * search group represented by the given searchId.
     * @param searchId
     * @param runFileName
     * @return
     */
    public abstract int loadRunIdForSearchAndFileName(int searchId, String runFileName);
    
    
    /**
     * Returns the number of locations with the given runId and serverDirectory.
     * @param runId
     * @param serverDirectory
     * @return
     */
    public abstract int loadMatchingRunLocations(int runId, String serverDirectory);
    
    
    public abstract void delete(int runId);
    
}
