package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;

public interface MsSearchResultProteinDAO {

    public abstract List<MsSearchResultProteinDb> loadResultProteins(int resultId);

    /**
     * @param proteinMatch
     * @param resultId database id of the search result this protein match is associated with
     */
    public abstract void save(MsSearchResultProtein proteinMatch, int resultId);

    public abstract void delete(int resultId);

    public abstract void saveAll(List<MsSearchResultProteinDb> proteinMatchList);

}