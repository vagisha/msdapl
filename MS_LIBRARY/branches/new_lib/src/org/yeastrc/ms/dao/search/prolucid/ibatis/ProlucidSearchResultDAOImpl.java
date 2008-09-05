/**
 * ProlucidSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchResultDAOImpl extends BaseSqlMapDAO implements
ProlucidSearchResultDAO {

    private MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao;

    public ProlucidSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }

    @Override
    public ProlucidSearchResultDb load(int resultId) {
        return (ProlucidSearchResultDb) queryForObject("ProlucidResult.select", resultId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return resultDao.loadResultIdsForRunSearch(runSearchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId,
            int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }


    @Override
    public int save(int searchId, String searchDbName, ProlucidSearchResult searchResult,
            int runSearchId, int scanId) {
        // first save the base result
        int resultId = resultDao.save(searchId, searchDbName, searchResult, runSearchId, scanId);

        // now save the ProLuCID specific information
        ProlucidResultDataSqlMapParam resultDb = new ProlucidResultDataSqlMapParam(resultId, searchResult.getProlucidResultData());
        save("ProlucidResult.insert", resultDb);
        return resultId;
    }

    @Override
    public int saveResultOnly(ProlucidSearchResult searchResult,
            int runSearchId, int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        int resultId = resultDao.saveResultOnly(searchResult, runSearchId, scanId);

        // now save the ProLuCID specific information
        ProlucidResultDataSqlMapParam resultDb = new ProlucidResultDataSqlMapParam(resultId, searchResult.getProlucidResultData());
        save("ProlucidResult.insert", resultDb);

        return resultId;
    }

    // resultID, 
    // spRank,
    // XCorrRank,
    // sp,
    // binomialProbability,
    // XCorr,
    // ZScore 
    // deltaCN, 
    // calculatedMass,
    // matchingIons,
    // predictedIons)
    @Override
    public void saveAllProlucidResultData(
            List<ProlucidResultDataDb> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (ProlucidResultDataDb data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getSpRank() == -1 ? "NULL" : data.getSpRank());
            values.append(",");
            values.append(data.getxCorrRank()== -1 ? "NULL" : data.getxCorrRank());
            values.append(",");
            values.append(data.getSp());
            values.append(",");
            values.append(data.getBinomialProbability());
            values.append(",");
            values.append(data.getxCorr());
            values.append(",");
            values.append(data.getZscore());
            values.append(",");
            values.append(data.getDeltaCN());
            values.append(",");
            values.append(data.getCalculatedMass());
            values.append(",");
            values.append(data.getMatchingIons() == -1 ? "NULL" : data.getMatchingIons());
            values.append(",");
            values.append(data.getPredictedIons() == -1 ? "NULL" : data.getPredictedIons()  );
            values.append(")");
        }
        values.deleteCharAt(0);

        System.out.println(values.toString());
        save("ProlucidResult.insertAll", values.toString());
    }

    @Override
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    public static final class ProlucidResultDataSqlMapParam implements ProlucidResultDataDb {

        private int resultId;
        private ProlucidResultData result;

        public ProlucidResultDataSqlMapParam(int resultId, ProlucidResultData result) {
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

        public Double getBinomialProbability() {
            return result.getBinomialProbability();
        }

        public Double getZscore() {
            return result.getZscore();
        }
    }
}
