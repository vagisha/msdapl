package org.yeastrc.ms.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public interface SequestSearchResultDAO extends GenericSearchResultDAO<SequestSearchResultIn, SequestSearchResult> {

    public abstract void saveAllSequestResultData(List<SequestResultDataWId> dataList);
}
