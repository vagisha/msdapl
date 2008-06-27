package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.MS2FileHeader;

public interface MS2FileHeaderDAO {

    public abstract boolean save(MS2FileHeader headers);

    public abstract List<MS2FileHeader> loadHeadersForRun(int runId);

}