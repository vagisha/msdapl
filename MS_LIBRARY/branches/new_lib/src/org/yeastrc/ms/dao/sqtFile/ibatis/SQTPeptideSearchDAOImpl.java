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
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAO;
import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.db.MsPeptideSearch;
import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.sqtFile.ISQTSearch;
import org.yeastrc.ms.domain.sqtFile.db.SQTPeptideSearch;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTPeptideSearchDAOImpl extends BaseSqlMapDAO 
    implements MsPeptideSearchDAO<ISQTSearch, SQTPeptideSearch> {

    private MsPeptideSearchDAO<IMsSearch, MsPeptideSearch> searchDao;
    private SQTSearchHeaderDAO headerDao;
    private SQTSpectrumDataDAO spectrumDao;
    
    public SQTPeptideSearchDAOImpl(SqlMapClient sqlMap,
            MsPeptideSearchDAO<IMsSearch, MsPeptideSearch> searchDao,
            SQTSearchHeaderDAO headerDao,
            SQTSpectrumDataDAO spectrumDao) {
        super(sqlMap);
        this.searchDao = searchDao;
        this.headerDao = headerDao;
        this.spectrumDao = spectrumDao;
    }
    
    public SQTPeptideSearch loadSearch(int searchId) {
        return (SQTPeptideSearch) queryForObject("MsSearch.select", searchId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForRun(int runId) {
        return searchDao.loadSearchIdsForRun(runId);
    }
    
    public List<SQTPeptideSearch> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int saveSearch (ISQTSearch search, int runId) {
        
        // save the search
        int searchId = searchDao.saveSearch(search, runId);
        
        // save the headers
        for (IHeader h: search.getHeaders()) {
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
