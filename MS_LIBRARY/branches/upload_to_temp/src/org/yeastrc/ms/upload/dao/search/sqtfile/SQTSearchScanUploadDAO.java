package org.yeastrc.ms.upload.dao.search.sqtfile;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

public interface SQTSearchScanUploadDAO {

    public abstract SQTSearchScan load(int runSearchId, int scanId, int charge);
    
    public abstract void saveAll(List<SQTSearchScan> scanDataList);

    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;

}