/**
 * ProlucidSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParamDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchDAOImpl extends BaseSqlMapDAO implements MsSearchDAO <ProlucidSearch, ProlucidSearchDb> {

    
private MsSearchDAO<MsSearch, MsSearchDb> searchDao;
    
    public ProlucidSearchDAOImpl(SqlMapClient sqlMap, MsSearchDAO<MsSearch, MsSearchDb> searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public ProlucidSearchDb loadSearch(int searchId) {
        return (ProlucidSearchDb) queryForObject("ProlucidSearch.select", searchId);
    }
    
    public int saveSearch(ProlucidSearch search) {
        
        int searchId = searchDao.saveSearch(search);
        // save ProLuCID search parameters
        for (ProlucidParam param: search.getProlucidParams()) {
            // insert top level elements with parentID=0
            insertProlucidParam(param, 0, searchId);
        }
        return searchId;
    }
    
    // recursively insert all the param elements
    private void insertProlucidParam(ProlucidParam param, int parentParamId, int searchId) {
        int paramId = saveAndReturnId("ProlucidSearch.insertParams", new ProlucidParamSqlMapParam(searchId, parentParamId, param));
        for (ProlucidParam child: param.getChildParamElements()) {
            insertProlucidParam(child, paramId, searchId);
        }
    }
    
    @Override
    public int updateSearchAnalysisProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchAnalysisProgramVersion(searchId, versionStr);
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }
    
    public static final class ProlucidParamSqlMapParam implements ProlucidParamDb {

        private int searchId;
        private int parentId;
        private String elName;
        private String elValue;
        
        public ProlucidParamSqlMapParam(int searchId, int parentId, ProlucidParam param) {
            this.searchId = searchId;
            this.elName = param.getParamElementName();
            this.elValue = param.getParamElementValue();
        }
        
        public int getSearchId() {
            return searchId;
        }
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by ProlucidParamSqlMapParam");
        }

        @Override
        public String getParamElementName() {
            return elName;
        }

        @Override
        public String getParamElementValue() {
            return elValue;
        }

        @Override
        public int getParentParamElementId() {
            return parentId;
        }
    }
}
