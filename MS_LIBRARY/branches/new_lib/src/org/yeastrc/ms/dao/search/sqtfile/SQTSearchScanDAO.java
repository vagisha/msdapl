package org.yeastrc.ms.dao.search.sqtfile;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScanDb load(int searchId, int scanId, int charge);
    
    public abstract void save(SQTSearchScan scanData, int searchId, int scanId);

    public abstract void deleteForSearch(int searchId);

}