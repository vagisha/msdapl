package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.dto.sqtFile.SQTPeptideSearch;

public interface SQTPeptideSearchDAO extends MsPeptideSearchDAO {

    public abstract SQTPeptideSearch loadSearch(int searchId);

    public abstract List<SQTPeptideSearch> loadSearchesForRun(int runId);

    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public abstract int saveSearch(SQTPeptideSearch search);

    /**
     * Deletes the search and any SQT search headers associated with the run.
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);

}