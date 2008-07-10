package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.ms2File.db.MS2FileHeader;

public interface MS2FileHeaderDAO {

    public abstract void save(IHeader header, int runId);

    public abstract List<MS2FileHeader> loadHeadersForRun(int runId);
    
    public abstract void deleteHeadersForRunId(int runId);
    
    public abstract void deleteHeadersForRunIds(List<Integer> runIds);

}