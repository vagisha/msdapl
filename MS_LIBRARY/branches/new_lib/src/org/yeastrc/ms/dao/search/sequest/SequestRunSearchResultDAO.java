package org.yeastrc.ms.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.MsRunSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResultDb;

public interface SequestRunSearchResultDAO extends MsRunSearchResultDAO<SequestRunSearchResult, SequestRunSearchResultDb> {

    public abstract void saveAllSequestResultData(List<SequestResultDataDb> dataList);
}
