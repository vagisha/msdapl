package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.MsSearchResultProteinDAOImpl.MsResultProteinSqlMapParam;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.MsSearchResultProteinDb;

public interface MsSearchResultProteinDAO {

    public abstract List<MsSearchResultProteinDb> loadResultProteins(int resultId);

    /**
     * 
     * @param proteinMatch
     * @param resultId database id of the search result this protein match is associated with
     */
    public abstract void save(MsSearchResultProtein proteinMatch, int resultId);

    public abstract void delete(int resultId);

    public abstract void saveAll(List<MsResultProteinSqlMapParam> proteinMatchList);

}