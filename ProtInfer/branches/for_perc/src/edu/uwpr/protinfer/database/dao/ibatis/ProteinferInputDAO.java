package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferInputDAO;
import edu.uwpr.protinfer.database.dto.ProteinferInput;

public class ProteinferInputDAO extends BaseSqlMapDAO implements GenericProteinferInputDAO<ProteinferInput>{

    private static final String sqlMapNameSpace = "ProteinferInput";
    
    public ProteinferInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public List<ProteinferInput> loadProteinferInputList(int pinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinferInputList", pinferId);
    }
    
    public List<Integer> loadInputIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectRunSearchIds", pinferId);
    }
    
    public int saveProteinferInput(ProteinferInput input) {
        return saveAndReturnId(sqlMapNameSpace+".saveProteinferInput", input);
    }
    
    public void deleteProteinferInput(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
