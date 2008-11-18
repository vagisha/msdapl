package edu.uwpr.protinfer.database.dao;

import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinferInput;

public class ProteinferInputDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferInput";
    
    public ProteinferInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveProteinferInputList(List<ProteinferInput> inputList) {
        if(inputList.size() == 0)
            return;
        StringBuilder buf = new StringBuilder();
        for(ProteinferInput input: inputList) {
            buf.append(",("+input.getProteinferId()+",");
            buf.append(input.getRunSearchId()+",");
            if(input.getNumTargetHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumTargetHits()+",");
            if(input.getNumDecoyHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumDecoyHits()+",");
            if(input.getNumFilteredTargetHits() == -1)
                buf.append("NULL)");
            else
               buf.append(input.getNumFilteredTargetHits()+")");
        }
        buf.deleteCharAt(0);
//        System.out.println(buf.toString());
        super.save(sqlMapNameSpace+".saveProteinferInputList", buf.toString());
    }
    
    public void saveProteinferInput(ProteinferInput input) {
        super.save(sqlMapNameSpace+".saveProteinferInput", input);
    }
    
    public List<Integer> getRunSearchIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectProteinferInput", pinferId);
    }
    
    public void deleteProteinferInput(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
