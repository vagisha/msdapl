package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;

public interface MsSearchResultProteinDAO {

    public abstract List<MsSearchResultProtein> loadResultProteins(int resultId);

    public abstract boolean resultProteinExists(int resultId, int proteinId);
    /**
     * @param protein
     * @param sequenceDbName -- name of the database used for the search.
     * @param resultId database id of the search result associated with this protein match
     */
    public abstract void save(MsSearchResultProteinIn protein, String sequenceDbName, int resultId);
    
    /**
     * @param protein
     * @param sequenceDbId -- id (from nrseq's tblDatabase) of the database used for the search
     * @param resultId
     */
    public abstract void save(MsSearchResultProteinIn protein, int sequenceDbId, int resultId);

    public abstract void delete(int resultId);

    public abstract void saveAll(List<MsSearchResultProtein> proteinMatchList);

}