package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.domain.sqtFile.ISQTSearch;
import org.yeastrc.ms.domain.sqtFile.db.SQTPeptideSearch;

public interface SQTPeptideSearchDAO extends MsPeptideSearchDAO<ISQTSearch> {

    public abstract SQTPeptideSearch loadSearch(int searchId);

    public abstract List<SQTPeptideSearch> loadSearchesForRun(int runId);

    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
   // public abstract int saveSearch(ISQTPeptideSearch search);

}