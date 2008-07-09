/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsPeptideSearch;
import org.yeastrc.ms.dto.IMsSearchDynamicMod;
import org.yeastrc.ms.dto.IMsSearchMod;
import org.yeastrc.ms.dto.IMsSequenceDatabase;
import org.yeastrc.ms.dto.MsPeptideSearch;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsPeptideSearchDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchDAO {

    public MsPeptideSearchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public IMsPeptideSearch loadSearch(int searchId) {
        return (IMsPeptideSearch) queryForObject("MsSearch.select", searchId);
    }
    
    public List<? extends MsPeptideSearch> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    public List<Integer> loadSearchIdsForRun(int runId) {
        return queryForList("MsSearch.selectSearchIdsForRun", runId);
    }
    
    public int saveSearch(IMsPeptideSearch search) {
        int searchId = saveAndReturnId("MsSearch.insert", search);
        
        // save any database information associated with the search 
        MsSequenceDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
        for (IMsSequenceDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
        MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        for (IMsSearchMod staticMod: search.getStaticModifications()) {
            staticMod.setSearchId(searchId);
            modDao.saveStaticModification(staticMod);
        }
        
        // save any dynamic modifications used for the search
        for (IMsSearchDynamicMod dynaMod: search.getDynamicModifications()) {
            dynaMod.setSearchId(searchId);
            modDao.saveDynamicModification(dynaMod);
        }
        
        return searchId;
    }
    
    public void deleteSearch(int searchId) {
        
        // delete all results for this search
        MsPeptideSearchResultDAO resultDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
        resultDao.deleteResultsForSearch(searchId);
        
        // delete any sequence database(s) associated with this search
        MsSequenceDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
        seqDbDao.deleteSearchDatabases(searchId);
        
        // delete any static and dynamic modifications used for this search
        MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        modDao.deleteStaticModificationsForSearch(searchId);
        modDao.deleteDynamicModificationsForSearch(searchId);
        
        // finally delete the search
        delete("MsSearch.delete", searchId);
    }

}
