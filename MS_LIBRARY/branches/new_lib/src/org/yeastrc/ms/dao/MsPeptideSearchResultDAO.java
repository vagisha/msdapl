package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.IMsSearchResult;
import org.yeastrc.ms.domain.db.MsPeptideSearchResult;

public interface MsPeptideSearchResultDAO<I extends IMsSearchResult, O extends MsPeptideSearchResult> {

    public abstract O load(int resultId);

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
    public abstract int save(I searchResult, int searchId, int scanId);

    /**
     * Deletes this search result (msPeptideSearchResult table) along with any 
     * associated protein matches (msProteinMatch)
     * and dynamic modifications (msDynamicModResult).
     * @param resultId
     */
    public abstract void delete(int resultId);
    
    
    public abstract void deleteResultsForSearch(int searchId);
    
}