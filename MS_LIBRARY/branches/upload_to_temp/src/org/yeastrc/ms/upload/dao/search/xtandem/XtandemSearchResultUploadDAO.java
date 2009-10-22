package org.yeastrc.ms.upload.dao.search.xtandem;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;
import org.yeastrc.ms.upload.dao.search.GenericSearchResultUploadDAO;

public interface XtandemSearchResultUploadDAO extends GenericSearchResultUploadDAO {

    public abstract void saveAllXtandemResultData(List<XtandemResultDataWId> dataList);

    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
   
}
