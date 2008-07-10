package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsPeptideSearchResult;

public interface MsPeptideSearchResultDAO {

    public abstract MsPeptideSearchResult load(int resultId);

    public abstract List<Integer> loadResultIdsForSearch(int searchId);
    
    /**
     * Saves the search result in the msPeptideSearchResult table. 
     * Any associated protein matches for this result are also saved in the 
     * msProteinMatch table.
     * Any dynamic modifications associated with this result are saved in 
     * the msDynamicModResult table
     * 
     * @param searchResult
     * @return id (in msPeptideSearchResult) for this search result
     */
    public abstract int save(MsPeptideSearchResult searchResult);

    /**
     * Deletes this search result (msPeptideSearchResult table) along with any 
     * associated protein matches (msProteinMatch)
     * and dynamic modifications (msDynamicModResult).
     * @param resultId
     */
    public abstract void delete(int resultId);
    
    
    public abstract void deleteResultsForSearch(int searchId);
    
}