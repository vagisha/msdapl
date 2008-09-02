package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;

public interface MsSearchResultProteinDAO {

    public abstract List<MsSearchResultProteinDb> loadResultProteins(int resultId);

    public abstract boolean resultProteinExists(int resultId, int proteinId);
    /**
     * @param resultProtein
     * @param searchDbName -- name of the database used for the search.
     * @param resultId database id of the search result associated with this protein match
     */
    public abstract void save(MsSearchResultProtein protein, String searchDbName, int resultId);

    public abstract void delete(int resultId);

    public abstract void saveAll(List<MsSearchResultProteinDb> proteinMatchList);

}