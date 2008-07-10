/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.IMsSearchResult;
import org.yeastrc.ms.domain.db.MsPeptideSearchResult;
import org.yeastrc.ms.domain.sqtFile.ISQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsPeptideSearchResultDAO<ISQTSearchResult, SQTSearchResult> {

    private MsPeptideSearchResultDAO<IMsSearchResult, MsPeptideSearchResult> resultDao;
    
    public SQTSearchResultDAOImpl(SqlMapClient sqlMap,
            MsPeptideSearchResultDAO<IMsSearchResult, MsPeptideSearchResult> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SQTSearchResult load(int resultId) {
        return (SQTSearchResult) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(ISQTSearchResult sqtResult, int searchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(sqtResult, searchId, scanId);
        
        // now save the SQT specific information
        SQTSearchResultDb resultDb = new SQTSearchResultDb(resultId, sqtResult);
        save("SqtResult.insert", resultDb);
        return resultId;
    }
    
    /**
     * Deletes the search result and any SQT specific information associated with the result
     * @param resultId
     */
    public void delete(int resultId) {
        // delete SQT specific data
        delete("SqtResult.delete", resultId);
        // delete the parent result entry
        resultDao.delete(resultId);
    }

    @Override
    public void deleteResultsForSearch(int searchId) {
        List<Integer> resultIds = loadResultIdsForSearch(searchId);
        for (Integer resultId: resultIds)
            delete(resultId);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return resultDao.loadResultIdsForSearch(searchId);
    }

    
}
