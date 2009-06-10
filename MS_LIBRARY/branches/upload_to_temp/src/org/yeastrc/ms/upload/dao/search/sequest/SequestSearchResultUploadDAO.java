package org.yeastrc.ms.upload.dao.search.sequest;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.upload.dao.search.GenericSearchResultUploadDAO;

public interface SequestSearchResultUploadDAO extends GenericSearchResultUploadDAO {

    public abstract void saveAllSequestResultData(List<SequestResultDataWId> dataList);

    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}
