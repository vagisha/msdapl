/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestSearchResultBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchResultDAOImpl extends BaseSqlMapDAO implements SequestSearchResultDAO {

    private MsSearchResultDAO resultDao;
    
    public SequestSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SequestSearchResult load(int resultId) {
        return (SequestSearchResult) queryForObject("SequestResult.select", resultId);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return resultDao.loadResultIdsForRunSearch(runSearchId);
    }

    @Override
    public List<Integer> loadTopResultIdsForRunSearch(int runSearchId) {
        return queryForList("SequestResult.selectTopResultIdsForRunSearch", runSearchId);
    }
    
    @Override
    public List<SequestSearchResult> loadTopResultsWProteinsForRunSearchN(int runSearchId) {
        return queryForList("SequestResult.selectTopResultsWProteinsForRunSearchN", runSearchId);
    }
    
//    @Override
//    public List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId) {
//        return queryForList("SequestResult.selectTopResultsForRunSearchN", runSearchId);
//    }
    
    
    
    public List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            conn = super.getConnection();
            String sql = "SELECT * from msRunSearchResult as res, SQTSearchResult as sres WHERE"+
                         " res.id = sres.resultID AND sres.XCorrRank = 1 AND res.runSearchID = ?";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            List<SequestSearchResult> resultList = new ArrayList<SequestSearchResult>();
            
            while ( rs.next() ) {
            
                SequestSearchResultBean result = new SequestSearchResultBean();
                result.setId(rs.getInt("id"));
                result.setRunSearchId(rs.getInt("runSearchID"));
                result.setScanId(rs.getInt("scanID"));
                result.setCharge(rs.getInt("charge"));
                SearchResultPeptideBean peptide = new SearchResultPeptideBean();
                peptide.setPeptideSequence(rs.getString("peptide"));
                peptide.setPreResidue(rs.getString("preResidue").charAt(0));
                peptide.setPostResidue(rs.getString("postResidue").charAt(0));
                result.setResultPeptide(peptide);
                result.setValidationStatus(ValidationStatus.instance(rs.getString("validationStatus").charAt(0)));
                result.setSp(rs.getBigDecimal("sp"));
                result.setSpRank(rs.getInt("spRank"));
                result.setxCorr(rs.getBigDecimal("XCorr"));
                result.setxCorrRank(rs.getInt("XCorrRank"));
                result.setDeltaCN(rs.getBigDecimal("deltaCN"));
                result.setEvalue(rs.getDouble("evalue"));
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
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }
    
    public int save(int searchId, SequestSearchResultIn searchResult, int runSearchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(searchId, searchResult, runSearchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SequestResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(SequestSearchResultIn searchResult, int runSearchId,
            int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        int resultId = resultDao.saveResultOnly(searchResult, runSearchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataSqlMapParam resultDb = new SequestResultDataSqlMapParam(resultId, searchResult.getSequestResultData());
        save("SequestResult.insert", resultDb);
        
        return resultId;
    }
    
    @Override
    public void saveAllSequestResultData(List<SequestResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( SequestResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
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
            values.append(data.getEvalue());
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

    public static final class SequestResultDataSqlMapParam implements SequestResultDataWId {
        
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

        public Double getEvalue() {
            return result.getEvalue();
        }
    }
}
