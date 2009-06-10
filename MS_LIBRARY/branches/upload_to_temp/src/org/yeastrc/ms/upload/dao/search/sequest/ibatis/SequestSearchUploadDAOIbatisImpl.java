/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sequest.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchUploadDAOIbatisImpl extends BaseSqlMapDAO implements SequestSearchUploadDAO {

    private MsSearchUploadDAO searchDao;
    
    public SequestSearchUploadDAOIbatisImpl(SqlMapClient sqlMap, MsSearchUploadDAO searchDao) {
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
