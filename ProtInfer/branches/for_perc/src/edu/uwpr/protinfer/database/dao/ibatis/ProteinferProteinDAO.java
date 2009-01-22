package edu.uwpr.protinfer.database.dao.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.impl.SequestSearchResultBean;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public class ProteinferProteinDAO extends BaseSqlMapDAO implements GenericProteinferProteinDAO<ProteinferProtein> {

    private static final String sqlMapNameSpace = "ProteinferProtein";

    public ProteinferProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(GenericProteinferProtein<?> protein) {
        return saveAndReturnId(sqlMapNameSpace+".insert", protein);
    }
    
    public void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("pinferProteinId", pinferProteinId);
        map.put("pinferPeptideId", pinferPeptideId);
        super.save(sqlMapNameSpace+".insertPeptideProteinMatch", map);
    }
    
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("annotation", annotation);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserAnnotation", map);
    }
    
    public void updateUserValidation(int pinferProteinId, ProteinUserValidation validation) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        String validationStr = validation == null ? null : String.valueOf(validation.getStatusChar());
        map.put("userValidation", validationStr);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserValidation", map);
    }
    
    public ProteinferProtein loadProtein(int pinferProteinId) {
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> loadProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> loadProteinsN(int pinferId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = super.getConnection();
            String sql = "SELECT * from msProteinInferProtein WHERE piRunID=?";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, pinferId );
            rs = stmt.executeQuery();
            
            List<ProteinferProtein> proteinList = new ArrayList<ProteinferProtein>();
            
            while ( rs.next() ) {
            
                ProteinferProtein protein = new ProteinferProtein();
                protein.setId(rs.getInt("id"));
                protein.setProteinferId(rs.getInt("piRunID"));
                protein.setNrseqProteinId(rs.getInt("nrseqProteinID"));
                protein.setCoverage(rs.getDouble("coverage"));
                protein.setUserAnnotation(rs.getString("userAnnotation"));
                String validationStr = rs.getString("userValidation");
                ProteinUserValidation validation = ProteinUserValidation.UNVALIDATED;
                if(validationStr != null && validationStr.length() > 0) {
                    validation = ProteinUserValidation.getStatusForChar(validationStr.charAt(0));
                }
                protein.setUserValidation(validation);
                proteinList.add(protein);
            }
            
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return proteinList;
          
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
    
    public int getProteinCount(int proteinferId) {
       return (Integer) queryForObject(sqlMapNameSpace+".selectProteinCountForProteinferRun", proteinferId); 
    }
    
    public void delete(int pinferProteinId) {
        super.delete(sqlMapNameSpace+".delete", pinferProteinId);
    }
    
    /**
     * Type handler for converting between ProteinUserValidation and SQL's CHAR type.
     */
    public static final class UserValidationTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToUserValidation(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinUserValidation validation = (ProteinUserValidation) parameter;
            if (validation == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(validation.getStatusChar()));
        }

        public Object valueOf(String s) {
            return stringToUserValidation(s);
        }
        
        private ProteinUserValidation stringToUserValidation(String validationStr) {
            if (validationStr == null)
                return null;
            if (validationStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+validationStr+" to ProteinUserValidation");
            ProteinUserValidation userValidation = ProteinUserValidation.getStatusForChar(Character.valueOf(validationStr.charAt(0)));
            if (userValidation == null)
                throw new IllegalArgumentException("Invalid ProteinUserValidation value: "+validationStr);
            return userValidation;
        }
    }
}
