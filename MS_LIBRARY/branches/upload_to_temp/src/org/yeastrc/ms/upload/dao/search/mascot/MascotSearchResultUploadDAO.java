package org.yeastrc.ms.upload.dao.search.mascot;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;
import org.yeastrc.ms.upload.dao.search.GenericSearchResultUploadDAO;

public interface MascotSearchResultUploadDAO extends GenericSearchResultUploadDAO {

    public abstract void saveAllMascotResultData(List<MascotResultDataWId> dataList);

    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
   
}
