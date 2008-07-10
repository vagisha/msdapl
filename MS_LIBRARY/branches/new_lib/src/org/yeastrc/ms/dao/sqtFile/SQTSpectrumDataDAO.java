package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.domain.sqtFile.SQTSpectrumData;

public interface SQTSpectrumDataDAO {

    public abstract SQTSpectrumData load(int searchId, int scanId, int charge);

    public abstract void save(SQTSpectrumData scanData);

    public abstract void deleteForSearch(int searchId);

}