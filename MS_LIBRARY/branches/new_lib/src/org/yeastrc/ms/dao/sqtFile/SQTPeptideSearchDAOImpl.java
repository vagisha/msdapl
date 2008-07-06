/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.dao.BaseSqlMapDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.dto.sqtFile.SQTPeptideSearch;
import org.yeastrc.ms.dto.sqtFile.SQTSearchHeader;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTPeptideSearchDAOImpl extends BaseSqlMapDAO implements SQTPeptideSearchDAO {

    public SQTPeptideSearchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTPeptideSearch load(int searchId) {
        return (SQTPeptideSearch) queryForObject("MsSearch.select", searchId);
    }
    
    public List<SQTPeptideSearch> loadSearchForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int save (SQTPeptideSearch search) {
        
        // save the search
        MsPeptideSearchDAO searchDao = DAOFactory.instance().getMsPeptideSearchDAO();
        int searchId = searchDao.saveSearch(search);
        
        
        // save the headers first
        SQTSearchHeaderDAO headerDao = DAOFactory.instance().getSqtHeaderDAO();
        List<SQTSearchHeader> headers = search.getHeaders();
        for (SQTSearchHeader h: headers) {
            h.setSearchId(searchId);
            headerDao.saveSQTHeader(h);
        }
        
        return searchId;
    }
    
    /**
     * Deletes the search and any SQT search headers associated with the run.
     * @param searchId
     */
    public void delete (int searchId) {
        // delete headers first
        SQTSearchHeaderDAO headerDao = DAOFactory.instance().getSqtHeaderDAO();
        headerDao.deleteSQTHeadersForSearch(searchId);
        
        // now delete the search
        MsPeptideSearchDAO searchDao = DAOFactory.instance().getMsPeptideSearchDAO();
        searchDao.deleteSearch(searchId);
    }
}
