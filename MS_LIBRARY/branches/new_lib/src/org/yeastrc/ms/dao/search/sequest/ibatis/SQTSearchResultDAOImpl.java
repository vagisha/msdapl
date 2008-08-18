/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SQTSearchResultDAO;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchResultDAOImpl extends BaseSqlMapDAO implements SQTSearchResultDAO {

    private MsSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao;
    
    public SQTSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SequestRunSearchResultDb load(int resultId) {
        return (SequestRunSearchResultDb) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(SequestRunSearchResult sqtResult, int searchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(sqtResult, searchId, scanId);
        
        // now save the SQT specific information
        SQTSearchResultSqlMapParam resultDb = new SQTSearchResultSqlMapParam(resultId, sqtResult);
        save("SqtResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(SequestRunSearchResult searchResult, int searchId,
            int scanId) {
        int resultId = resultDao.saveResultOnly(searchResult, searchId, scanId);
        
        // now save the SQT specific information
        SQTSearchResultSqlMapParam resultDb = new SQTSearchResultSqlMapParam(resultId, searchResult);
        save("SqtResult.insert", resultDb);
        
        return resultId;
    }
    
    @Override
    public void saveSqtResultOnly(SequestRunSearchResult searchResult, int resultId) {
        // now save the SQT specific information
        SQTSearchResultSqlMapParam resultDb = new SQTSearchResultSqlMapParam(resultId, searchResult);
        save("SqtResult.insert", resultDb);
    }
    
    
    public void saveAllSqtResultScores(List<SQTSearchResultScoresDb> resultList) {
        if (resultList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (SQTSearchResultScoresDb result: resultList) {
            values.append("(");
            values.append(result.getResultId());
            values.append(",");
            values.append(result.getxCorrRank());
            values.append(",");
            values.append(result.getSpRank());
            values.append(",");
            values.append(result.getDeltaCN());
            values.append(",");
            values.append(result.getxCorr());
            values.append(",");values.append(result.getSp());
            values.append("),");
        }
        values.deleteCharAt(values.length() - 1);
        
        save("SqtResult.insertAll", values.toString());
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

    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int searchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(searchId, scanId, charge);
    }
    
    public class SQTSearchResultSqlMapParam {
        
        private int resultId;
        private SequestRunSearchResult result;
        
        public SQTSearchResultSqlMapParam(int resultId, SequestRunSearchResult result) {
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
