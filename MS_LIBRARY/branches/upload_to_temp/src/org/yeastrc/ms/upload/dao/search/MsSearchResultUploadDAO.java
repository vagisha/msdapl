package org.yeastrc.ms.upload.dao.search;

import java.sql.SQLException;

public interface MsSearchResultUploadDAO extends GenericSearchResultUploadDAO {
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}