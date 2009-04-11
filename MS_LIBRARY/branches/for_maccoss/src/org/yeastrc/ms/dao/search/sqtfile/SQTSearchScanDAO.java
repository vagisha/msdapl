package org.yeastrc.ms.dao.search.sqtfile;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScan load(int runSearchId, int scanId, int charge);
    
    public abstract void save(SQTSearchScan scanData);
    
    public abstract void saveAll(List<SQTSearchScan> scanDataList);

    public abstract void delete(int runSearchId, int scanId, int charge);
    
    public abstract void deleteForRunSearch(int runSearchId);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;

}