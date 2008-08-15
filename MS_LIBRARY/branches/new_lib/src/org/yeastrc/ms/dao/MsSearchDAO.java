package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsRunSearch;
import org.yeastrc.ms.domain.MsRunSearchDb;

public interface MsSearchDAO <I extends MsRunSearch, O extends MsRunSearchDb>{

    public abstract O loadSearch(int searchId);
    
    public abstract List<O> loadSearchesForRun(int runId);

    public abstract List<Integer> loadSearchIdsForExperiment(int experimentId);
    
    public abstract List<Integer> loadSearchIdsForRun(int runId);
    
    public abstract int loadSearchIdForRunAndExperiment(int runId, int experimentId);
    
    /**
     * Saves the search in the database. Also saves:
     * 1. any associated sequence database information used for the search
     * 2. any static modifications used for the search
     * 3. any dynamic modifications used for the search 
     * @param search
     * @param runId
     * @param experimentId
     * @return database id of the search
     */
    public abstract int saveSearch(I search, int runId, int experimentId);

    /**
     * Deletes the search ONLY
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);

}