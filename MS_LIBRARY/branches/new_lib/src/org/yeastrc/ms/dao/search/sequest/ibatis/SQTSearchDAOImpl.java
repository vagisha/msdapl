/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchDAOImpl extends BaseSqlMapDAO 
    implements MsSearchDAO<SQTRunSearch, SQTRunSearchDb> {

    private MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao;
    private SQTHeaderDAO headerDao;
    private SQTSearchScanDAO spectrumDao;
    private MsSearchResultDAO<SequestRunSearchResult, SequestRunSearchResultDb> resultDao;
    
    public SQTSearchDAOImpl(SqlMapClient sqlMap,
            MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao,
            SQTHeaderDAO headerDao,
            SQTSearchScanDAO spectrumDao,
            MsSearchResultDAO<SequestRunSearchResult, SequestRunSearchResultDb> resultDao) {
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
