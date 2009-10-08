/**
 * MascotSearchDAOImpl.java
 * @author Vagisha Sharma
 * Oct 05, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.mascot.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.mascot.MascotSearch;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.mascot.MascotSearchUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MascotSearchUploadDAOIbatisImpl extends BaseSqlMapDAO implements MascotSearchUploadDAO {

    private MsSearchUploadDAO searchDao;
    
    public MascotSearchUploadDAOIbatisImpl(SqlMapClient sqlMap, MsSearchUploadDAO searchDAO) {
        super(sqlMap);
        this.searchDao = searchDAO;
    }
    
    public MascotSearch loadSearch(int searchId) {
        return (MascotSearch) queryForObject("MascotSearch.select", searchId);
    }
    
    public int saveSearch(MascotSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        
        // save Mascot search parameters
        try {
            for (Param param: search.getMascotParams()) {
                save("MascotSearch.insertParams", new MascotParamSqlMapParam(searchId, param));
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
    
    public static final class MascotParamSqlMapParam implements Param {

        private int searchId;
        private Param param;
        
        public MascotParamSqlMapParam(int searchId, Param param) {
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