/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> {

    private MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao;
    
    public SQTSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SQTSearchResultDb load(int resultId) {
        return (SQTSearchResultDb) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(SQTSearchResult sqtResult, int searchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(sqtResult, searchId, scanId);
        
        // now save the SQT specific information
        SQTSearchResultSqlMapParam resultDb = new SQTSearchResultSqlMapParam(resultId, sqtResult);
        save("SqtResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(SQTSearchResult searchResult, int searchId,
            int scanId) {
        int resultId = resultDao.saveResultOnly(searchResult, searchId, scanId);
        
        // now save the SQT specific information
        SQTSearchResultSqlMapParam resultDb = new SQTSearchResultSqlMapParam(resultId, searchResult);
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

    public class SQTSearchResultSqlMapParam {
        
        private int resultId;
        private SQTSearchResult result;
        
        public SQTSearchResultSqlMapParam(int resultId, SQTSearchResult result) {
            this.resultId = resultId;
            this.result = result;
        }

        public int getResultId() {
            return resultId;
        }

        public BigDecimal getDeltaCN() {
            return result.getDeltaCN();
        }

        public BigDecimal getSp() {
            return result.getSp();
        }

        public int getSpRank() {
            return result.getSpRank();
        }

        public BigDecimal getxCorr() {
            return result.getxCorr();
        }

        public int getxCorrRank() {
            return result.getxCorrRank();
        }
    }
}
