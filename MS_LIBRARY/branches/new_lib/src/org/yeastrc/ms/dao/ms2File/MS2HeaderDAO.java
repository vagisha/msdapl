package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2HeaderDb;

public interface MS2HeaderDAO {

    public abstract void save(MS2Field header, int runId);

    public abstract List<MS2HeaderDb> loadHeadersForRun(int runId);
    
    public abstract void deleteHeadersForRunId(int runId);
    
    public abstract void deleteHeadersForRunIds(List<Integer> runIds);

}