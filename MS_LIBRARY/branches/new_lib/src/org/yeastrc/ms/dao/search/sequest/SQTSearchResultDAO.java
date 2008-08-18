package org.yeastrc.ms.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResultDb;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResultScoresDb;

public interface SQTSearchResultDAO extends MsSearchResultDAO<SequestRunSearchResult, SQTSearchResultDb> {

    public abstract void saveSqtResultOnly(SequestRunSearchResult searchResult, int resultId);
    
    public abstract void saveAllSqtResultScores(List<SQTSearchResultScoresDb> resultList);
}
