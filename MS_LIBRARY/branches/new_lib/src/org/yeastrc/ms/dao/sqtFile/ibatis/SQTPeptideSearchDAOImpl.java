/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO;
import org.yeastrc.ms.domain.sqtFile.SQTPeptideSearch;
import org.yeastrc.ms.domain.sqtFile.db.ISQTPeptideSearch;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTPeptideSearchDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchDAO<ISQTPeptideSearch> {

    public SQTPeptideSearchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTPeptideSearch loadSearch(int searchId) {
//        return (SQTPeptideSearch) super.loadSearch(searchId);
        return (SQTPeptideSearch) queryForObject("MsSearch.select", searchId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForRun(int runId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public List<SQTPeptideSearch> loadSearchesForRun(int runId) {
//        return (List<SQTPeptideSearch>) super.loadSearchesForRun(runId);
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int saveSearch (ISQTPeptideSearch search) {
        
        // save the search
//        int searchId = super.saveSearch(search);
        
        // save the headers
        SQTSearchHeaderDAO headerDao = DAOFactory.instance().getSqtHeaderDAO();
//        List<IHeader> headers = search.getHeaders();
//        for (SQTSearchHeader h: headers) {
//            h.setSearchId(searchId);
//            headerDao.saveSQTHeader(h);
//        }
        
//        return searchId;
        return 0;
    }
    
    /**
     * Deletes the search and any SQT search headers associated with the run.
     * @param searchId
     */
    public void deleteSearch (int searchId) {
        // delete headers first
        SQTSearchHeaderDAO headerDao = DAOFactory.instance().getSqtHeaderDAO();
        headerDao.deleteSQTHeadersForSearch(searchId);
        
        // now delete the search
//        super.deleteSearch(searchId);
    }
  
}
