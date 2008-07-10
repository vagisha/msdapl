/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.dao.MsPeptideSearchModDAO;
import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dao.MsSequenceDatabaseDAO;
import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.IMsSearchDatabase;
import org.yeastrc.ms.domain.IMsSearchModification;
import org.yeastrc.ms.domain.IMsSearchResult;
import org.yeastrc.ms.domain.db.MsPeptideSearch;
import org.yeastrc.ms.domain.db.MsPeptideSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsPeptideSearchDAOImpl extends BaseSqlMapDAO 
        implements MsPeptideSearchDAO<IMsSearch, MsPeptideSearch> {

    private MsSequenceDatabaseDAO seqDbDao;
    private MsPeptideSearchModDAO modDao;
    private MsPeptideSearchResultDAO<IMsSearchResult, MsPeptideSearchResult> resultDao;
    
    public MsPeptideSearchDAOImpl(SqlMapClient sqlMap, 
            MsPeptideSearchResultDAO<IMsSearchResult, MsPeptideSearchResult> resultDao,
            MsSequenceDatabaseDAO seqDbDao,
            MsPeptideSearchModDAO modDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
    }
    
    public MsPeptideSearch loadSearch(int searchId) {
        return (MsPeptideSearch) queryForObject("MsSearch.select", searchId);
    }
    
    public List<MsPeptideSearch> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    public List<Integer> loadSearchIdsForRun(int runId) {
        return queryForList("MsSearch.selectSearchIdsForRun", runId);
    }
    
    public int saveSearch(IMsSearch search, int runId) {
        MsSearchDb searchDb = new MsSearchDb(runId, search);
        int searchId = saveAndReturnId("MsSearch.insert", searchDb);
        
        // save any database information associated with the search 
        for (IMsSearchDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
        for (IMsSearchModification staticMod: search.getStaticModifications()) {
            modDao.saveStaticModification(staticMod, searchId);
        }
        
        // save any dynamic modifications used for the search
        for (IMsSearchModification dynaMod: search.getDynamicModifications()) {
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
