package edu.uwpr.protinfer.database.dao.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferProteinDAO extends BaseSqlMapDAO implements 
    GenericProteinferProteinDAO<ProteinferSpectrumMatch, ProteinferPeptide, ProteinferProtein> {

    private static final String sqlMapNameSpace = "ProteinferProtein";

    private ProteinferPeptideDAO peptDao;
    
    public ProteinferProteinDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap);
        this.peptDao = peptDao;
    }

    public int save(BaseProteinferProtein<?, ?> protein) {
        return saveAndReturnId(sqlMapNameSpace+".insert", protein);
    }
    
    public int saveProteinferProtein(ProteinferProtein protein) {
        int proteinId = save(protein);
        for(ProteinferPeptide peptide: protein.getPeptides()) {
            int peptideId = peptide.getId();
            // save only if it has not been saved before
            if(peptideId == 0) {
                peptideId = peptDao.saveProteinferPeptide(peptide);
            }
            saveProteinferProteinPeptideMatch(proteinId, peptideId);
        }
        return proteinId;
    }
    
    public void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferProteinId", pinferProteinId);
        map.put("pinferPeptideId", pinferPeptideId);
        super.save(sqlMapNameSpace+".insertPeptideProteinMatch", map);
    }
    
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("annotation", annotation);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserAnnotation", map);
    }
    
    public void updateUserValidation(int pinferProteinId, ProteinUserValidation validation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userValidation", String.valueOf(validation.getStatusChar()));
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserValidation", map);
    }
    
    public ProteinferProtein getProtein(int pinferProteinId) {
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> getProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public int getFilteredProteinCount(int proteinferId) {
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
