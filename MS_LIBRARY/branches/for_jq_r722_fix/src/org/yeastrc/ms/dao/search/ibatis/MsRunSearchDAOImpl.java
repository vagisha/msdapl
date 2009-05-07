/**
 * MsRunSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.SearchFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsRunSearchDAOImpl extends BaseSqlMapDAO implements MsRunSearchDAO {
    
    public MsRunSearchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public MsRunSearch loadRunSearch(int runSearchId) {
        return (MsRunSearch) queryForObject("MsRunSearch.select", runSearchId);
    }
    
    @Override
    public List<Integer> loadRunSearchIdsForSearch(int searchId) {
        return queryForList("MsRunSearch.selectRunSearchIdsForSearch", searchId);
    }
    
    public List<Integer> loadRunSearchIdsForRun(int runId) {
        return queryForList("MsRunSearch.selectRunSearchIdsForRun", runId);
    }
    
    @Override
    public int loadIdForRunAndSearch(int runId, int searchId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runId", runId);
        map.put("searchId", searchId);
        Integer runSearchId = (Integer)queryForObject("MsRunSearch.selectIdForRunAndSearch", map);
        if (runSearchId != null)
            return runSearchId;
        return 0;
    }
    
    public int saveRunSearch(MsRunSearch search) {
        return saveAndReturnId("MsRunSearch.insert", search);
    }
    
    public void deleteRunSearch(int runSearchId) {
        delete("MsRunSearch.delete", runSearchId);
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