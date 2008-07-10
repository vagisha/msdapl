package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.domain.sqtFile.ISQTSearchScan;
import org.yeastrc.ms.domain.sqtFile.db.SQTSpectrumData;

public interface SQTSpectrumDataDAO {

    public abstract SQTSpectrumData load(int searchId, int scanId, int charge);

    public abstract void save(ISQTSearchScan scanData, int searchId, int scanId);

    public abstract void deleteForSearch(int searchId);

}