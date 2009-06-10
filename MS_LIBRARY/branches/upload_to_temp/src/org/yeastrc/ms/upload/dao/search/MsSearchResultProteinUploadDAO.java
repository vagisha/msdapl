package org.yeastrc.ms.upload.dao.search;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultProtein;

public interface MsSearchResultProteinUploadDAO {

    public abstract void saveAll(List<MsSearchResultProtein> proteinMatchList);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;

}