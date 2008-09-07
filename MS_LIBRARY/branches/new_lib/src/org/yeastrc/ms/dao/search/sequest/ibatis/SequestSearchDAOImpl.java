/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.search.SearchProgram;
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
    
    public int saveSearch(SequestSearchIn search, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, sequenceDatabaseId);
        
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
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchProgramVersion(searchId, versionStr);
    }
    
    @Override
    public int updateSearchProgram(int searchId, SearchProgram program) {
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
