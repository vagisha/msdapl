package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsPeptideSearchResult;

public interface MsPeptideSearchResultDAO {

    public abstract MsPeptideSearchResult load(int id);

    public abstract int save(MsPeptideSearchResult searchResult);

    public abstract void delete(int id);

}