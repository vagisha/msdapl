package org.yeastrc.ms.dao.ms2File;

import org.yeastrc.ms.ms2File.Ms2FileHeaders;

public interface MS2FileRunHeadersDAO {

    public abstract void save(Ms2FileHeaders headers);

    public abstract Ms2FileHeaders loadHeadersForRun(int runId);

}