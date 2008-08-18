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
        return (SequestSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    public int saveSearch(SequestSearch search) {
        
        int searchId = searchDao.saveSearch(search);
        
        //TODO save Sequest parameters
        
        return searchId;
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }

}
