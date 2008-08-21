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
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchResultDAOImpl extends BaseSqlMapDAO implements SequestSearchResultDAO {

    private MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao;
    
    public SequestSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SequestSearchResultDb load(int resultId) {
        return (SequestSearchResultDb) queryForObject("SequestResult.select", resultId);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int searchId) {
        return resultDao.loadResultIdsForRunSearch(searchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int searchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(searchId, scanId, charge);
    }
    
    public int save(SequestSearchResult searchResult, String searchDbName, int searchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(searchResult, searchDbName, searchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SequestResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(SequestSearchResult searchResult, int searchId,
            int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        int resultId = resultDao.saveResultOnly(searchResult, searchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SequestResult.insert", resultDb);
        
        return resultId;
    }
    
    @Override
    public void saveAllSequestResultData(List<SequestResultDataDb> dataList) {
        if (dataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( SequestResultDataDb data: dataList) {
            values.append(",(");
            values.append(data.getResultId());
            values.append(",");
            values.append(data.getSp());
            values.append(",");
            int spRank = data.getSpRank();
            values.append(spRank == -1 ? "NULL" : spRank);
            values.append(",");
            values.append(data.getxCorr());
            values.append(",");
            int xcorrRank = data.getxCorrRank();
            values.append(xcorrRank == -1 ? "NULL" : xcorrRank);
            values.append(",");
            values.append(data.getDeltaCN());
            values.append(",");
            values.append(data.geteValue());
            values.append(",");
            values.append(data.getCalculatedMass());
            values.append(",");
            int mIons = data.getMatchingIons();
            values.append(mIons == -1 ? "NULL" : mIons);
            values.append(",");
            int pIons = data.getPredictedIons();
            values.append(pIons == -1 ? "NULL" : pIons);
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("SequestResult.insertAll", values.toString());
    }
    
    /**
     * Deletes the search result and any Sequest specific information associated with the result
     * @param resultId
     */
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    public static final class SequestResultDataSqlMapParam implements SequestResultDataDb {
        
        private int resultId;
        private SequestResultData result;
        
        public SequestResultDataSqlMapParam(int resultId, SequestResultData result) {
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

        public BigDecimal getCalculatedMass() {
            return result.getCalculatedMass();
        }

        public int getMatchingIons() {
            return result.getMatchingIons();
        }

        public int getPredictedIons() {
            return result.getPredictedIons();
        }

        public Double geteValue() {
            return result.geteValue();
        }
    }
}
