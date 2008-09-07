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
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTHeaderItemImpl;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTRunSearchDAOImpl extends BaseSqlMapDAO 
    implements SQTRunSearchDAO {

    private MsRunSearchDAO runSearchDao;
    private SQTHeaderDAO headerDao;
    
    public SQTRunSearchDAOImpl(SqlMapClient sqlMap,
            MsRunSearchDAO runSearchDao,
            SQTHeaderDAO headerDao) {
        super(sqlMap);
        this.runSearchDao = runSearchDao;
        this.headerDao = headerDao;
    }
    
    public SQTRunSearch loadRunSearch(int runSearchId) {
        return (SQTRunSearch) queryForObject("MsRunSearch.select", runSearchId);
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
     * @param runSearch
     * @return
     */
    public int saveRunSearch (SQTRunSearch runSearch) {
        
        // save the run_search
        int runSearchId = runSearchDao.saveRunSearch(runSearch);
        
        // save the headers
        for (SQTHeaderItem h: runSearch.getHeaders()) {
            headerDao.saveSQTHeader(new SQTHeaderItemImpl(h, runSearchId));
        }
        return runSearchId;
    }
    
    /**
     * Deletes the search
     * @param searchId
     */
    public void deleteRunSearch (int searchId) {
        runSearchDao.deleteRunSearch(searchId);
    }
}
