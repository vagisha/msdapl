package edu.uwpr.protinfer.database.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public class ProteinferProteinDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferProtein";

    
    public ProteinferProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveProteinferProtein(ProteinferProtein protein) {
        super.save(sqlMapNameSpace+".insert", protein);
    }
    
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("annotation", annotation);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserAnnotation", map);
    }
    
    public void updateUserValidation(int pinferProteinId, ProteinUserValidation validation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userValidation", validation.getStatusChar());
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserValidation", map);
    }
    
    public ProteinferProtein getProteinferProtein(int pinferId, int nrseqProtId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("nrseqProtId", nrseqProtId);
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".select", map);
    }
    
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> getProteinferProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> getProteinferClusterProteins(int pinferId,int clusterId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRunCluster", map);
    }
    
    public int getFilteredProteinCount(int proteinferId) {
       return (Integer) queryForObject(sqlMapNameSpace+".selectProteinCountForProteinferRun", proteinferId); 
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
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
