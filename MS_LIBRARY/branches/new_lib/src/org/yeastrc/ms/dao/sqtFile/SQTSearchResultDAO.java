package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchResult;

public interface SQTSearchResultDAO extends MsPeptideSearchResultDAO {

    public abstract SQTSearchResult load(int resultId);

    public abstract int save(SQTSearchResult sqtResult);

}