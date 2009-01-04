package edu.uwpr.protinfer.database.dao.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.GenericProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferInput;
import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferStatus;

public class ProteinferRunDAO extends BaseSqlMapDAO implements GenericProteinferRunDAO<ProteinferInput, ProteinferRun> {

    private static final String sqlMapNameSpace = "ProteinferRun";
    
    public ProteinferRunDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(GenericProteinferRun<?> run) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", run);
    }
    
    public void update(GenericProteinferRun<?> run) {
        super.update(sqlMapNameSpace+".update", run);
    }
    
    public ProteinferRun loadProteinferRun(int proteinferId) {
        return (ProteinferRun) super.queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds) {
        if(inputIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: inputIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        return super.queryForList(sqlMapNameSpace+".selectPinferIdsForInputIds", buf.toString());
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
    
    /**
     * Type handler for converting between ProteinferStatus and SQL's CHAR type.
     */
    public static final class ProteinferProgramTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToProteinferProgram(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinInferenceProgram program = (ProteinInferenceProgram) parameter;
            if (program == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(program.name());
        }

        public Object valueOf(String s) {
            return stringToProteinferProgram(s);
        }
        
        private ProteinInferenceProgram stringToProteinferProgram(String programStr) {
            if (programStr == null)
                throw new IllegalArgumentException("String representing ProteinInferenceProgram cannot be null");
            ProteinInferenceProgram program = ProteinInferenceProgram.getProgramForName(programStr);
            if (program == null)
                throw new IllegalArgumentException("Invalid ProteinInferenceProgram value: "+programStr);
            return program;
        }
    }
}
