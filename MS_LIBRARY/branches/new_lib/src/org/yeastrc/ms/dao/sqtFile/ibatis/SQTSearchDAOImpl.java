/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResult;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.sqtFile.SQTRunSearch;
import org.yeastrc.ms.domain.sqtFile.SQTRunSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchDAOImpl extends BaseSqlMapDAO 
    implements MsSearchDAO<SQTRunSearch, SQTRunSearchDb> {

    private MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao;
    private SQTHeaderDAO headerDao;
    private SQTSearchScanDAO spectrumDao;
    private MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> resultDao;
    
    public SQTSearchDAOImpl(SqlMapClient sqlMap,
            MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao,
            SQTHeaderDAO headerDao,
            SQTSearchScanDAO spectrumDao,
            MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> resultDao) {
        super(sqlMap);
        this.searchDao = searchDao;
        this.headerDao = headerDao;
        this.spectrumDao = spectrumDao;
        this.resultDao = resultDao;
    }
    
    public SQTRunSearchDb loadSearch(int searchId) {
        return (SQTRunSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForExperiment(int experimentId) {
        return searchDao.loadSearchIdsForExperiment(experimentId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForRun(int runId) {
        return searchDao.loadSearchIdsForRun(runId);
    }
    
    @Override
    public int loadSearchIdForRunAndExperiment(int runId, int searchGroupId) {
        return searchDao.loadSearchIdForRunAndExperiment(runId, searchGroupId);
    }
    
    public List<SQTRunSearchDb> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int saveSearch (SQTRunSearch search, int runId, int searchGroupId) {
        
        // save the search
        int searchId = searchDao.saveSearch(search, runId, searchGroupId);
        
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
        
        // delete the results
        resultDao.deleteResultsForSearch(searchId);
        
        // now delete the search
        searchDao.deleteSearch(searchId);
    }

}
