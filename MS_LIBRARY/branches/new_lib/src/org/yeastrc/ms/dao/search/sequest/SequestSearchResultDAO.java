package org.yeastrc.ms.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public interface SequestSearchResultDAO extends GenericSearchResultDAO<SequestSearchResultIn, SequestSearchResult> {

    public abstract void saveAllSequestResultData(List<SequestResultDataWId> dataList);
    
    public abstract List<Integer> loadTopResultIdsForRunSearch(int runSearchId);
    
    /**
     * Returns the search result along with its associated proteins.
     * @param runSearchId
     * @return
     */
    public abstract List<SequestSearchResult> loadTopResultsWProteinsForRunSearchN(int runSearchId);
    
    /**
     * Returns the search results without any associated proteins. 
     * @param runSearchId
     * @return
     */
    public abstract List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId);
}
