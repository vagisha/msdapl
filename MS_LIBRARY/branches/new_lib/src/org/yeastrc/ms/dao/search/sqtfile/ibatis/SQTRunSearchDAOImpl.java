/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTRunSearchDAOImpl extends BaseSqlMapDAO 
    implements MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> {

    private MsRunSearchDAO<MsRunSearch, MsRunSearchDb> runSearchDao;
    private SQTHeaderDAO headerDao;
    
    public SQTRunSearchDAOImpl(SqlMapClient sqlMap,
            MsRunSearchDAO<MsRunSearch, MsRunSearchDb> runSearchDao,
            SQTHeaderDAO headerDao) {
        super(sqlMap);
        this.runSearchDao = runSearchDao;
        this.headerDao = headerDao;
    }
    
    public SQTRunSearchDb loadRunSearch(int runSearchId) {
        return (SQTRunSearchDb) queryForObject("MsRunSearch.select", runSearchId);
    }
    
    public List<Integer> loadRunSearchIdsForSearch(int searchId) {
        return runSearchDao.loadRunSearchIdsForSearch(searchId);
    }
    
    public List<Integer> loadRunSearchIdsForRun(int runId) {
        return runSearchDao.loadRunSearchIdsForRun(runId);
    }
    
    public int loadIdForRunAndSearch(int runId, int searchId) {
        return runSearchDao.loadIdForRunAndSearch(runId, searchId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param search
     * @return
     */
    public int saveRunSearch (SQTRunSearch search, int runId, int searchGroupId) {
        
        // save the search
        int searchId = runSearchDao.saveRunSearch(search, runId, searchGroupId);
        
        // save the headers
        for (SQTField h: search.getHeaders()) {
            headerDao.saveSQTHeader(h, searchId);
        }
        return searchId;
    }
    
    /**
     * Deletes the search
     * @param searchId
     */
    public void deleteRunSearch (int searchId) {
        runSearchDao.deleteRunSearch(searchId);
    }
}
