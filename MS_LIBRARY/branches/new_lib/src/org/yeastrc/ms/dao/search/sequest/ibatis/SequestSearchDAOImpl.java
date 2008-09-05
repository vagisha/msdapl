/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchDAOImpl extends BaseSqlMapDAO implements MsSearchDAO <SequestSearch, SequestSearchDb> {

    private MsSearchDAO<MsSearch, MsSearchDb> searchDao;
    
    public SequestSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchDAO<MsSearch, MsSearchDb> searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public SequestSearchDb loadSearch(int searchId) {
        return (SequestSearchDb) queryForObject("SequestSearch.select", searchId);
    }
    
    public int saveSearch(SequestSearch search, int sequenceDatabaseId) {
        
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
