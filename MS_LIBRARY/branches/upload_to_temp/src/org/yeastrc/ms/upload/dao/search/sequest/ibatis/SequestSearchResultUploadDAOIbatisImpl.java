/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sequest.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchResultUploadDAOIbatisImpl extends BaseSqlMapDAO implements SequestSearchResultUploadDAO {

    private MsSearchResultUploadDAO resultDao;
    
    public SequestSearchResultUploadDAOIbatisImpl(SqlMapClient sqlMap,
            MsSearchResultUploadDAO resultDao) {
        super(sqlMap);
        this.resultDao = resultDao;
    }
    
    public SequestSearchResult load(int resultId) {
        return (SequestSearchResult) queryForObject("SequestResult.select", resultId);
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
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId,
            int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
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
            values.append(data.getDeltaCNstar());
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
    
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE SQTSearchResult DISABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    @Override
    public void enableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE SQTSearchResult ENABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
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

        public BigDecimal getDeltaCNstar() {
            return result.getDeltaCNstar();
        }
    }
}
