/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.sqtFile.SQTField;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchDAOImpl extends BaseSqlMapDAO 
    implements MsSearchDAO<SQTSearch, SQTSearchDb> {

    private MsSearchDAO<MsSearch, MsSearchDb> searchDao;
    private SQTHeaderDAO headerDao;
    private SQTSearchScanDAO spectrumDao;
    
    public SQTSearchDAOImpl(SqlMapClient sqlMap,
            MsSearchDAO<MsSearch, MsSearchDb> searchDao,
            SQTHeaderDAO headerDao,
            SQTSearchScanDAO spectrumDao) {
        super(sqlMap);
        this.searchDao = searchDao;
        this.headerDao = headerDao;
        this.spectrumDao = spectrumDao;
    }
    
    public SQTSearchDb loadSearch(int searchId) {
        return (SQTSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForRun(int runId) {
        return searchDao.loadSearchIdsForRun(runId);
    }
    
    public List<SQTSearchDb> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int saveSearch (SQTSearch search, int runId) {
        
        // save the search
        int searchId = searchDao.saveSearch(search, runId);
        
        // save the headers
        for (SQTField h: search.getHeaders()) {
            headerDao.saveSQTHeader(h, searchId);
        }
        
        return searchId;
    }
    
    /**
     * Deletes the search and any SQT search headers associated with the run.
     * @param searchId
     */
    public void deleteSearch (int searchId) {
        // delete headers first
        headerDao.deleteSQTHeadersForSearch(searchId);
        
        // delete the spectrum data
        spectrumDao.deleteForSearch(searchId);
        
        // now delete the search
        searchDao.deleteSearch(searchId);
    }
  
}
