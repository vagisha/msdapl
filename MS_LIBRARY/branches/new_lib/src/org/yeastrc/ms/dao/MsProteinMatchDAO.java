package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsSearchResultProtein;
import org.yeastrc.ms.dto.MsProteinMatch;

public interface MsProteinMatchDAO {

    public abstract List<MsProteinMatch> loadResultProteins(int resultId);

    /**
     * 
     * @param proteinMatch
     * @param resultId database id of the search result this protein match is associated with
     */
    public abstract void save(IMsSearchResultProtein proteinMatch, int resultId);

    public abstract void delete(int resultId);

}