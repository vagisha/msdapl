/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDatabase;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchDAOImpl extends BaseSqlMapDAO 
        implements MsSearchDAO<MsSearch, MsSearchDb> {

    private MsSearchDatabaseDAO seqDbDao;
    private MsSearchModificationDAO modDao;
    private MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao;
    
    public MsSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao,
            MsSearchDatabaseDAO seqDbDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
    }
    
    public MsSearchDb loadSearch(int searchId) {
        return (MsSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    public List<MsSearchDb> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    public List<Integer> loadSearchIdsForRun(int runId) {
        return queryForList("MsSearch.selectSearchIdsForRun", runId);
    }
    
    public int saveSearch(MsSearch search, int runId) {
        MsSearchSqlMapParam searchDb = new MsSearchSqlMapParam(runId, search);
        int searchId = saveAndReturnId("MsSearch.insert", searchDb);
        
        // save any database information associated with the search 
        for (MsSearchDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
        for (MsSearchModification staticMod: search.getStaticModifications()) {
            modDao.saveStaticModification(staticMod, searchId);
        }
        
        // save any dynamic modifications used for the search
        for (MsSearchModification dynaMod: search.getDynamicModifications()) {
            modDao.saveDynamicModification(dynaMod, searchId);
        }
        
        return searchId;
    }
    
    public void deleteSearch(int searchId) {
        
        // delete all results for this search
        resultDao.deleteResultsForSearch(searchId);
        
        // delete any sequence database(s) associated with this search
        seqDbDao.deleteSearchDatabases(searchId);
        
        // delete any static and dynamic modifications used for this search
        modDao.deleteStaticModificationsForSearch(searchId);
        modDao.deleteDynamicModificationsForSearch(searchId);
        
        // finally delete the search
        delete("MsSearch.delete", searchId);
    }

}
