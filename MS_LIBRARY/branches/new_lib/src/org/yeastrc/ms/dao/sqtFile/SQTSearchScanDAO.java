package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScanDb;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScanDb load(int searchId, int scanId, int charge);

    public abstract void save(SQTSearchScan scanData, int searchId, int scanId);

    public abstract void deleteForSearch(int searchId);

}