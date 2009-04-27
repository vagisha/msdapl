package org.yeastrc.ms.dao.search.sqtfile;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScan load(int runSearchId, int scanId, int charge);
    
    public abstract void save(SQTSearchScan scanData);

    public abstract void deleteForRunSearch(int runSearchId);

}