package org.yeastrc.ms.upload.dao.search.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsSearchResultUploadDAOIbatisImpl extends BaseSqlMapDAO 
        implements MsSearchResultUploadDAO {

    public MsSearchResultUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        map.put("peptide", peptide);
        return queryForList("MsSearchResult.selectResultForRunSearchScanChargePeptide", map);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId, int scanId,
            int charge, BigDecimal mass) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        map.put("observedMass", mass);
        Integer count = (Integer) queryForObject("MsSearchResult.countResultsForRunSearchScanChargeMass", map);
        if(count == null)
            return 0;
        return count;
    }
    
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId) {

//        MsSearchResultWrap resultDb = new MsSearchResultWrap(searchResult, runSearchId, scanId);
//        return saveAndReturnId("MsSearchResult.insert", resultDb);
        
        String sql = "INSERT INTO msRunSearchResult ( ";
        sql +=       "runSearchID, scanID, charge, observedMass, peptide, preResidue, postResidue, validationStatus )";
        sql +=       " VALUES (";
        
        // runSearchID
        if(runSearchId == 0)    sql +=   "NULL, ";
        else                    sql += runSearchId+", ";
        
        // scanID
        if(scanId == 0)         sql +=   "NULL, ";
        else                    sql +=   scanId+", ";
        
        // charge
        if(searchResult.getCharge() == 0)   sql +=   "NULL, ";
        else                                sql +=   searchResult.getCharge()+", ";
        
        // observedMass
        if(searchResult.getObservedMass() == null)  sql +=   "NULL, ";
        else                                        sql +=   searchResult.getObservedMass()+", ";
        
        // peptide
        String peptide = searchResult.getResultPeptide().getPeptideSequence();
        if(peptide == null) sql +=   "NULL, ";
        else                sql +=   "\""+peptide+"\", ";
        
        // preResidue
        String preResidue = Character.toString(searchResult.getResultPeptide().getPreResidue());
        if(preResidue == null)  sql +=   "NULL, ";
        else                    sql +=   "\""+preResidue+"\", ";
        
        // postResidue
        String postResidue = Character.toString(searchResult.getResultPeptide().getPostResidue());
        if(postResidue == null) sql +=   "NULL, ";
        else                    sql +=   "\""+postResidue+"\", ";
        
        // validationStatus
        ValidationStatus validationStatus = searchResult.getValidationStatus();
        if(validationStatus == null || validationStatus == ValidationStatus.UNKNOWN)
            sql +=   "NULL ";
        else
            sql +=   "\""+Character.toString(validationStatus.getStatusChar())+"\"";
        sql += ")";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
            rs = stmt.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("Failed to get auto_increment key for sql: "+sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
            try {if(rs != null) rs.close();}
            catch(SQLException e){}
        }
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        
        String sql = "INSERT INTO msRunSearchResult ";
        sql +=       "( runSearchID, scanID, charge, observedMass, peptide, preResidue, postResidue, validationStatus )";
        sql +=       " VALUES (?,?,?,?,?,?,?,?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            
            for(MsSearchResult result: results) {
                if(result.getRunSearchId() == 0)    stmt.setNull(1, Types.INTEGER);
                else                                stmt.setInt(1, result.getRunSearchId());
                
                if(result.getScanId() == 0)         stmt.setNull(2, Types.INTEGER);
                else                                stmt.setInt(2, result.getScanId());
                
                if(result.getCharge() == 0)      stmt.setNull(3, Types.INTEGER);
                else                                stmt.setInt(3, result.getCharge());
                
                stmt.setBigDecimal(4, result.getObservedMass());
                
                stmt.setString(5, result.getResultPeptide().getPeptideSequence());
                
                String preResidue = Character.toString(result.getResultPeptide().getPreResidue());
                stmt.setString(6, preResidue);
                
                String postResidue = Character.toString(result.getResultPeptide().getPostResidue());
                stmt.setString(7, postResidue);
                
                ValidationStatus validationStatus = result.getValidationStatus();
                if(validationStatus == null || validationStatus == ValidationStatus.UNKNOWN)
                    stmt.setNull(8, Types.CHAR);
                else
                    stmt.setString(8, Character.toString(validationStatus.getStatusChar()));
               
                stmt.addBatch();
            }
            
            int[] counts = stmt.executeBatch();
            conn.commit();
            
            int numInserted = 0;
            for(int cnt: counts)    numInserted += cnt;
            
            if(numInserted != results.size())
                throw new RuntimeException("Number of results inserted ("+numInserted+
                        ") does not equal number input ("+results.size()+")");
                
            
            // check that we inserted everything and get the generated ids
            rs = stmt.getGeneratedKeys();
            List<Integer> generatedKeys = new ArrayList<Integer>(results.size());
            while(rs.next())
                generatedKeys.add(rs.getInt(1));
            
            if(generatedKeys.size() != numInserted)
                throw new RuntimeException("Failed to get auto_increment key for all results inserted. Number of keys returned: "
                        +generatedKeys.size());
            
            return generatedKeys;
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); } catch (SQLException e){}
            if(stmt != null) try { stmt.close(); } catch (SQLException e){}
            if(conn != null) try { conn.close(); } catch (SQLException e){}
        }
    }
    

    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        delete("MsSearchResult.deleteForRunSearch", runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msRunSearchResult DISABLE KEYS";
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
            String sql = "ALTER TABLE msRunSearchResult ENABLE KEYS";
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
    

    /**
     * Type handler for converting between ValidationType and SQL's CHAR type.
     */
    public static final class ValidationStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToValidationStatus(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ValidationStatus status = (ValidationStatus) parameter;
            if (status == null || status == ValidationStatus.UNKNOWN)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(Character.toString(status.getStatusChar()));
        }

        public Object valueOf(String statusStr) {
            return stringToValidationStatus(statusStr);
        }

        private Object stringToValidationStatus(String statusStr) {
            if (statusStr == null)
                return ValidationStatus.UNKNOWN;
            if (statusStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert \""+statusStr+"\" to ValidationStatus");
            ValidationStatus status = ValidationStatus.instance(statusStr.charAt(0));
            if (status == ValidationStatus.UNKNOWN)
                throw new IllegalArgumentException("Unrecognized validation status: "+statusStr);
            return status;
        }
    }
    
}
