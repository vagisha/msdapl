package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.MS2FileHeader;

public interface MS2FileHeaderDAO {

    public abstract void save(MS2FileHeader header);

    public abstract List<MS2FileHeader> loadHeadersForRun(int runId);

}