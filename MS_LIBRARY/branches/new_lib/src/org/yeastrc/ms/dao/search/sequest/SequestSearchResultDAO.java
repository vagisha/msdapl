package org.yeastrc.ms.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultDb;

public interface SequestSearchResultDAO extends MsSearchResultDAO<SequestSearchResult, SequestSearchResultDb> {

    public abstract void saveAllSequestResultData(List<SequestResultDataDb> dataList);
}
