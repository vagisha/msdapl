package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;

public interface SQTSearchResultDAO extends MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> {

    public abstract void saveSqtResultOnly(SQTSearchResult searchResult, int resultId);
    
    public abstract void saveAllSqtResultOnly(List<SQTSearchResultDb> resultList);
}
