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
import org.yeastrc.ms.dao.search.MsRunSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestRunSearchResultDAO;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataDb;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestRunSearchResultDAOImpl extends BaseSqlMapDAO implements SequestRunSearchResultDAO {

    private MsRunSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao;
    
    public SequestRunSearchResultDAOImpl(SqlMapClient sqlMap,
            MsRunSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SequestRunSearchResultDb load(int resultId) {
        return (SequestRunSearchResultDb) queryForObject("SqtResult.select", resultId);
    }
    
    public int save(SequestRunSearchResult searchResult, String searchDbName, int searchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(searchResult, searchDbName, searchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SqtResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(SequestRunSearchResult searchResult, int searchId,
            int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        int resultId = resultDao.saveResultOnly(searchResult, searchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SqtResult.insert", resultDb);
        
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
    public List<Integer> loadResultIdsForRunSearch(int searchId) {
        return resultDao.loadResultIdsForRunSearch(searchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int searchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(searchId, scanId, charge);
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
