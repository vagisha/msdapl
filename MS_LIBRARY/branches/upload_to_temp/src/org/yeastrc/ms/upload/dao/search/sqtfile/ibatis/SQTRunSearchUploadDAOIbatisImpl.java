/**
 * SQTPeptideSearch.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTHeaderItemWrap;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTHeaderUploadDAO;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTRunSearchUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTRunSearchUploadDAOIbatisImpl extends BaseSqlMapDAO 
    implements SQTRunSearchUploadDAO {

    private MsRunSearchUploadDAO runSearchDao;
    private SQTHeaderUploadDAO headerDao;
    
    public SQTRunSearchUploadDAOIbatisImpl(SqlMapClient sqlMap,
            MsRunSearchUploadDAO runSearchDao,
            SQTHeaderUploadDAO headerDao) {
        super(sqlMap);
        this.runSearchDao = runSearchDao;
        this.headerDao = headerDao;
    }
    
    public SQTRunSearch loadRunSearch(int runSearchId) {
        return (SQTRunSearch) queryForObject("MsRunSearch.select", runSearchId);
    }
    
    @Override
    public int loadIdForSearchAndFileName(int searchId, String filename) {
        return runSearchDao.loadIdForSearchAndFileName(searchId, filename);
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
            headerDao.saveSQTHeader(new SQTHeaderItemWrap(h, runSearchId));
        }
        return runSearchId;
    }
    
}