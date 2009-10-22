/**
 * XtandemSearchResultUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Oct 21, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.xtandem.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class XtandemSearchResultUploadDAOIbatisImpl extends BaseSqlMapDAO implements XtandemSearchResultUploadDAO {

    private MsSearchResultUploadDAO resultDao;
    
    public XtandemSearchResultUploadDAOIbatisImpl(SqlMapClient sqlMap,
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
    public void saveAllXtandemResultData(List<XtandemResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( XtandemResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getRank() == 0 ? "NULL" : data.getRank());
            values.append(",");
            values.append(data.getHyperScore());
            values.append(",");
            values.append(data.getNextScore());
            values.append(",");
            values.append(data.getBscore());
            values.append(",");
            values.append(data.getYscore());
            values.append(",");
            values.append(data.getExpect());
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
        
        save("XtandemResult.insertAll", values.toString());
    }
    
    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE XtandemSearchResult DISABLE KEYS";
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
            String sql = "ALTER TABLE XtandemSearchResult ENABLE KEYS";
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
   
}
