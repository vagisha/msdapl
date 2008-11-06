/**
 * ProlucidSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResultDataWrap;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchResultDAOImpl extends BaseSqlMapDAO implements
ProlucidSearchResultDAO {

    private MsSearchResultDAO resultDao;

    public ProlucidSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }

    @Override
    public ProlucidSearchResult load(int resultId) {
        return (ProlucidSearchResult) queryForObject("ProlucidResult.select", resultId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return resultDao.loadResultIdsForRunSearch(runSearchId);
    }

    @Override
    public List<Integer> loadTopResultIdsForRunSearch(int runSearchId) {
        return queryForList("ProlucidResult.selectTopResultIdsForRunSearch", runSearchId);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId,
            int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }


    @Override
    public int save(int searchId, ProlucidSearchResultIn searchResult, int runSearchId, int scanId) {
        // first save the base result
        int resultId = resultDao.save(searchId, searchResult, runSearchId, scanId);

        // now save the ProLuCID specific information
        ProlucidResultDataWrap resultDb = new ProlucidResultDataWrap(searchResult.getProlucidResultData(), resultId);
        save("ProlucidResult.insert", resultDb);
        return resultId;
    }

    @Override
    public int saveResultOnly(ProlucidSearchResultIn searchResult,
            int runSearchId, int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        int resultId = resultDao.saveResultOnly(searchResult, runSearchId, scanId);

        // now save the ProLuCID specific information
        ProlucidResultDataWrap resultDb = new ProlucidResultDataWrap(searchResult.getProlucidResultData(), resultId);
        save("ProlucidResult.insert", resultDb);

        return resultId;
    }

    /**
     * resultID, 
        primaryScoreRank,
        secondaryScoreRank,
        primaryScore,
        secondaryScore,
        deltaCN, 
        calculatedMass,
        matchingIons,
        predictedIons
     */
    @Override
    public void saveAllProlucidResultData(
            List<ProlucidResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (ProlucidResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getPrimaryScoreRank() == -1 ? "NULL" : data.getPrimaryScoreRank());
            values.append(",");
            values.append(data.getSecondaryScoreRank()== -1 ? "NULL" : data.getSecondaryScoreRank());
            values.append(",");
            values.append(data.getPrimaryScore());
            values.append(",");
            values.append(data.getSecondaryScore());
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

        save("ProlucidResult.insertAll", values.toString());
    }

    @Override
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }
}
