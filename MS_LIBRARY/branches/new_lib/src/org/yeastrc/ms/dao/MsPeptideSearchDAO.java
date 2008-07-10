package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.MsPeptideSearch;

public interface MsPeptideSearchDAO <T extends IMsSearch>{

    public abstract MsPeptideSearch loadSearch(int searchId);
    
    public abstract List<? extends MsPeptideSearch> loadSearchesForRun(int runId);

    public abstract List<Integer> loadSearchIdsForRun(int runId);

    /**
     * Saves the search in the database. Also saves:
     * 1. any associated sequence database information used for the search
     * 2. any static modifications used for the search
     * 3. any dynamic modifications used for the search 
     * @param search
     * @return database id of the search
     */
    public abstract int saveSearch(T search);

    /**
     * Deletes the search. Also deletes: 
     * 1. All results associated with this search
     * 2. Any sequence database associations with this search
     * 3. All static and dynamic modifications for the search
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);

}