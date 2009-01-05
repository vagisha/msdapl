/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchDAOImpl extends BaseSqlMapDAO implements SequestSearchDAO {

    private MsSearchDAO searchDao;
    
    public SequestSearchDAOImpl(SqlMapClient sqlMap, MsSearchDAO searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public SequestSearch loadSearch(int searchId) {
        return (SequestSearch) queryForObject("SequestSearch.select", searchId);
    }
    
    public int saveSearch(SequestSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        
        // save sequest search parameters
        try {
            for (SequestParam param: search.getSequestParams()) {
                save("SequestSearch.insertParams", new SequestParamSqlMapParam(searchId, param));
            }
        }
        catch(RuntimeException e) {
           deleteSearch(searchId);
           throw e;
        }
        return searchId;
    }
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        return searchDao.getSearchIdsForExperiment(experimentId);
    }
    
    @Override
    public MassType getFragmentMassType(int searchId) {
        String val = getSearchParamValue(searchId, "mass_type_fragment");
        if (val == null)            return null;
        if (val.equals("0"))        return MassType.AVG;
        else if (val.equals("1"))   return MassType.MONO;
        return null; // we don't recognize this value
    }

    @Override
    public MassType getParentMassType(int searchId) {
        String val = getSearchParamValue(searchId, "mass_type_parent");
        if (val == null)            return null;
        if (val.equals("0"))        return MassType.AVG;
        else if (val.equals("1"))   return MassType.MONO;
        return null; // we don't recognize this value
    }
    
    @Override
    public String getSearchParamValue(int searchId, String paramName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("searchId", searchId);
        map.put("paramName", paramName);
        return (String) queryForObject("SequestSearch.selectSearchParamValue", map);
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchProgramVersion(searchId, versionStr);
    }
    
    @Override
    public int updateSearchProgram(int searchId, Program program) {
        return searchDao.updateSearchProgram(searchId, program);
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }

    
    public static final class SequestParamSqlMapParam implements SequestParam {

        private int searchId;
        private SequestParam param;
        
        public SequestParamSqlMapParam(int searchId, SequestParam param) {
            this.searchId = searchId;
            this.param = param;
        }
        
        public int getSearchId() {
            return searchId;
        }
        @Override
        public String getParamName() {
            return param.getParamName();
        }

        @Override
        public String getParamValue() {
            return param.getParamValue();
        }
    }
   
}
