package edu.uwpr.protinfer.database.dao;

import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

public class ProteinferInputDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferInput";
    
    public ProteinferInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveProteinferInput(int pinferId, List<Integer> runSearchIdList) {
        if(runSearchIdList.size() == 0)
            return;
        StringBuilder buf = new StringBuilder();
        for(Integer id: runSearchIdList)
            buf.append(",("+pinferId+","+id+")");
        buf.deleteCharAt(0);
        super.save(sqlMapNameSpace+".saveProteinferInputIds", buf.toString());
    }
    
    public List<Integer> getRunSearchIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectRunSearchIds", pinferId);
    }
    
    public void deleteProteinferInput(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
