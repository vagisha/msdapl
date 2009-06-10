/**
 * ProlucidSearchResultUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.prolucid.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchResultUploadDAOIbatisImpl extends BaseSqlMapDAO implements
ProlucidSearchResultUploadDAO {

    private MsSearchResultUploadDAO resultDao;

    public ProlucidSearchResultUploadDAOIbatisImpl(SqlMapClient sqlMap,
            MsSearchResultUploadDAO resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }

    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        return resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId,
            int scanId, int charge, BigDecimal mass) {
        return resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
    }
    
    @Override
    public int saveResultOnly(MsSearchResultIn searchResult,
            int runSearchId, int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }

    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
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
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
}
