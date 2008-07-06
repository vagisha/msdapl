package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.dto.sqtFile.SQTPeptideSearch;

public interface SQTPeptideSearchDAO {

    public abstract SQTPeptideSearch load(int searchId);

    public abstract List<SQTPeptideSearch> loadSearchForRun(int runId);

    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public abstract int save(SQTPeptideSearch search);

    /**
     * Deletes the search and any SQT search headers associated with the run.
     * @param searchId
     */
    public abstract void delete(int searchId);

}