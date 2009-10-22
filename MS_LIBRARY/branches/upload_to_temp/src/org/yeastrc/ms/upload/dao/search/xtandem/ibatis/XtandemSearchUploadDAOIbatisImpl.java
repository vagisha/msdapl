/**
 * MascotSearchDAOImpl.java
 * @author Vagisha Sharma
 * Oct 05, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.xtandem.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearch;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class XtandemSearchUploadDAOIbatisImpl extends BaseSqlMapDAO implements XtandemSearchUploadDAO {

    private MsSearchUploadDAO searchDao;
    
    public XtandemSearchUploadDAOIbatisImpl(SqlMapClient sqlMap, MsSearchUploadDAO searchDAO) {
        super(sqlMap);
        this.searchDao = searchDAO;
    }
    
    public XtandemSearch loadSearch(int searchId) {
        return (XtandemSearch) queryForObject("XtandemSearch.select", searchId);
    }
    
    public int saveSearch(XtandemSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        
        // save Mascot search parameters
        try {
            for (Param param: search.getXtandemParams()) {
                save("XtandemSearch.insertParams", new XtandemParamSqlMapParam(searchId, param));
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
    
    public static final class XtandemParamSqlMapParam implements Param {

        private int searchId;
        private Param param;
        
        public XtandemParamSqlMapParam(int searchId, Param param) {
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