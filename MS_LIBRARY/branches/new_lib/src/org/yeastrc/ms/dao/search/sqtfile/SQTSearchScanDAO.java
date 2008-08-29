package org.yeastrc.ms.dao.search.sqtfile;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScanDb load(int runSearchId, int scanId, int charge);
    
    public abstract void save(SQTSearchScan scanData, int runSearchId, int scanId);

    public abstract void deleteForRunSearch(int runSearchId);

}