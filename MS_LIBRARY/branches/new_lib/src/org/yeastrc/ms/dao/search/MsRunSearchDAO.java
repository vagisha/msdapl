/**
 * MsRunSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;

/**
 * 
 */
public interface MsRunSearchDAO <I extends MsRunSearch, O extends MsRunSearchDb> {

    public abstract O loadRunSearch(int searchId);
    
    public abstract List<O> loadSearchesForRun(int runId);

    /**
     * Returns the database ids of individual run searches in a search group.
     * @param searchId
     * @return
     */
    public abstract List<Integer> loadRunSearchIdsForSearch(int searchId);
    
    /**
     * Returns the database ids of all searches on a run with the given 
     * database id. 
     * @param runId
     * @return
     */
    public abstract List<Integer> loadRunSearchIdsForRun(int runId);
    
    /**
     * Returns the database id of a search on the given run, and belonging 
     * to the given search group.
     * @param runId
     * @param searchId
     * @return
     */
    public abstract int loadIdForRunAndSearch(int runId, int searchId);
    
    /**
     * Saves the search (for a single run) in the database.
     * @param search
     * @param runId
     * @param searchId
     * @return database id of the search
     */
    public abstract int saveRunSearch(I search, int runId, int searchId);

    /**
     * Deletes the search (for a single run).
     * @param searchId
     */
    public abstract void deleteRunSearch(int searchId);
}
