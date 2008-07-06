package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsPeptideSearch;

public interface MsPeptideSearchDAO {

    public abstract MsPeptideSearch loadSearch(int searchId);
    
    public abstract List<MsPeptideSearch> loadSearchesForRun(int runId);

    public abstract List<Integer> loadSearchIdsForRun(int runId);

    /**
     * Saves the search in the database. Also saves:
     * 1. any associated sequence database information used for the search
     * 2. any static modifications used for the search
     * 3. any dynamic modifications used for the search 
     * @param search
     * @return database id of the search
     */
    public abstract int saveSearch(MsPeptideSearch search);

    /**
     * Deletes the search. Also deletes: 
     * 1. All results associated with this search
     * 2. Any sequence database associations with this search
     * 3. All static and dynamic modifications for the search
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);

}