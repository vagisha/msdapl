/**
 * MsRunSearchUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsRunSearchUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsRunSearchUploadDAO {
    
    public MsRunSearchUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public MsRunSearch loadRunSearch(int runSearchId) {
        return (MsRunSearch) queryForObject("MsRunSearch.select", runSearchId);
    }
    
    @Override
    public int loadIdForSearchAndFileName(int searchId, String filename) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("searchId", searchId);
        map.put("filename", filename);
        Integer runSearchId = (Integer)queryForObject("MsRunSearch.selectIdForSearchAndFile", map);
        if (runSearchId != null)
            return runSearchId;
        return 0;
    }
    
    public int saveRunSearch(MsRunSearch search) {
        return saveAndReturnId("MsRunSearch.insert", search);
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between SearchFileFormat and JDBC's VARCHAR types. 
     */
    public static class SearchFileFormatTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String format = getter.getString();
            if (getter.wasNull())
                return SearchFileFormat.UNKNOWN;
            return SearchFileFormat.instance(format);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(((SearchFileFormat)parameter).name());
        }

        public Object valueOf(String s) {
            return SearchFileFormat.instance(s);
        }
    }

    //---------------------------------------------------------------------------------------
}
