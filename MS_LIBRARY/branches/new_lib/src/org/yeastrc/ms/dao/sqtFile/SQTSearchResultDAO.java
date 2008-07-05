package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dto.sqtFile.SQTSearchResult;

public interface SQTSearchResultDAO {

    public abstract SQTSearchResult loadSQTResult(int resultId);

    public abstract int save(SQTSearchResult sqtResult);

    public abstract void deleteSQTResult(int resultId);

}