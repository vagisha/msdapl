package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinferInput;

public class ProteinferInputDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferInput";
    
    public ProteinferInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public <T extends ProteinferInput> void saveProteinferInput(T input) {
        super.save(sqlMapNameSpace+".saveProteinferInput", input);
    }
    
    public List<Integer> getRunSearchIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectProteinferInput", pinferId);
    }
    
    public void deleteProteinferInput(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
