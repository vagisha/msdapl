package edu.uwpr.protinfer.database.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferStatus;

public class ProteinferRunDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferRun";
    
    public ProteinferRunDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveNewProteinferRun() { 
        ProteinferRun run = new ProteinferRun();
        run.setStatus(ProteinferStatus.PENDING);
        return super.saveAndReturnId(sqlMapNameSpace+".insert", run);
    }
    
    public void setProteinferStatus(int proteinferId, ProteinferStatus status) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pinferId", proteinferId);
        map.put("status", String.valueOf(status.getStatusChar()));
        super.update(sqlMapNameSpace+".updateStatus", map);
    }
    
    public void setProteinferCompletionDate(int proteinferId, Date date) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pinferId", proteinferId);
        map.put("dateCompleted", date);
        super.update(sqlMapNameSpace+".updateDateCompleted", map);
    }
    
    public void setProteinferUnfilteredProteinCount(int proteinferId, int proteinCount) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", proteinferId);
        map.put("proteinCount", proteinCount);
        super.update(sqlMapNameSpace+".updateUnfilteredProteinCount", map);
    }
    
    public ProteinferRun getProteinferRun(int proteinferId) {
        return (ProteinferRun) super.queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    public List<Integer> getProteinferIdsForRunSearches(List<Integer> runSearchIds) {
        if(runSearchIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: runSearchIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        return super.queryForList(sqlMapNameSpace+".selectPinferIdsForRunSearchIds", buf.toString());
    }
    
    public void delete(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
    
    /**
     * Type handler for converting between ProteinferStatus and SQL's CHAR type.
     */
    public static final class ProteinferStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToProteinferStatus(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinferStatus status = (ProteinferStatus) parameter;
            if (status == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(status.getStatusChar()));
        }

        public Object valueOf(String s) {
            return stringToProteinferStatus(s);
        }
        
        private ProteinferStatus stringToProteinferStatus(String statusStr) {
            if (statusStr == null)
                throw new IllegalArgumentException("String representing ProteinferStatus cannot be null");
            if (statusStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+statusStr+" to ProteinferStatus");
            ProteinferStatus status = ProteinferStatus.getStatusForChar(Character.valueOf(statusStr.charAt(0)));
            if (status == null)
                throw new IllegalArgumentException("Invalid ProteinferStatus value: "+statusStr);
            return status;
        }
    }
}
