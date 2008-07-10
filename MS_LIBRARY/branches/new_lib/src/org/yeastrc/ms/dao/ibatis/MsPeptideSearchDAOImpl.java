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
import org.yeastrc.ms.domain.MsPeptideSearch;
import org.yeastrc.ms.domain.MsSequenceDatabase;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsPeptideSearchDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchDAO<MsPeptideSearch> {

    public MsPeptideSearchDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
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
    
    public int saveSearch(MsPeptideSearch search) {
        int searchId = saveAndReturnId("MsSearch.insert", search);
        
        // save any database information associated with the search 
        MsSequenceDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
        for (MsSequenceDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
//        MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
//        List<? extends IMsSearchMod> mods = search.getStaticModifications();
//        for (IMsSearchMod staticMod: mods) {
//            staticMod.setSearchId(searchId);
//            modDao.saveStaticModification(staticMod);
//        }
//        
//        // save any dynamic modifications used for the search
//        for (MsSearchDynamicMod dynaMod: search.getDynamicModifications()) {
//            dynaMod.setSearchId(searchId);
//            modDao.saveDynamicModification(dynaMod);
//        }
        
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
        MsPeptideSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        modDao.deleteStaticModificationsForSearch(searchId);
        modDao.deleteDynamicModificationsForSearch(searchId);
        
        // finally delete the search
        delete("MsSearch.delete", searchId);
    }

}
