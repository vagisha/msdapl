/**
 * ProlucidSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResultDataWrap;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidSearchResultBean;

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
    
//    @Override
//    public List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId) {
//        return queryForList("ProlucidResult.selectTopResultsForRunSearchN", runSearchId);
//    }
    
    /**
     * Returns the top hits (XCorr rank = 1) for a search. If multiple rank=1 hits
     * are found for a scan + charge combination return only one. 
     */
    public List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            conn = super.getConnection();
            String sql = "SELECT * from msRunSearchResult as res, ProLuCIDSearchResult as pres WHERE"+
                         " res.id = pres.resultID AND pres.primaryScoreRank=1 AND res.runSearchID = ?"+
                         " GROUP BY res.scanID, res.charge ORDER BY res.id";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            List<ProlucidSearchResult> resultList = new ArrayList<ProlucidSearchResult>();
            
            while ( rs.next() ) {
            
                ProlucidSearchResultBean result = new ProlucidSearchResultBean();
                result.setId(rs.getInt("id"));
                result.setRunSearchId(rs.getInt("runSearchID"));
                result.setScanId(rs.getInt("scanID"));
                result.setCharge(rs.getInt("charge"));
                SearchResultPeptideBean peptide = new SearchResultPeptideBean();
                peptide.setPeptideSequence(rs.getString("peptide"));
                String preRes = rs.getString("preResidue");
                if(preRes != null)
                    peptide.setPreResidue(preRes.charAt(0));
                String postRes = rs.getString("postResidue");
                if(postRes != null)
                    peptide.setPostResidue(postRes.charAt(0));
                result.setResultPeptide(peptide);
                String vStatus = rs.getString("validationStatus");
                if(vStatus != null)
                    result.setValidationStatus(ValidationStatus.instance(vStatus.charAt(0)));
                result.setPrimaryScore(rs.getDouble("primaryScore"));
                result.setPrimaryScoreRank(rs.getInt("primaryScoreRank"));
                result.setSecondaryScore(rs.getDouble("secondaryScore"));
                result.setSecondaryScoreRank(rs.getInt("secondaryScoreRank"));
                result.setDeltaCN(rs.getBigDecimal("deltaCN"));
                result.setCalculatedMass(rs.getBigDecimal("calculatedMass"));
                result.setMatchingIons(rs.getInt("matchingIons"));
                result.setPredictedIons(rs.getInt("predictedIons"));
                
                resultList.add(result);
            
            }
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return resultList;
            
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
        return null;
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
