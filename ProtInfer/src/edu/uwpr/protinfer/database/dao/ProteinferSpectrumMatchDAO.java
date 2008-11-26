package edu.uwpr.protinfer.database.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferSpectrumMatchDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferSpectrumMatch";

    public ProteinferSpectrumMatchDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveSpectrumMatch(ProteinferSpectrumMatch spectrumMatch) {
        super.save(sqlMapNameSpace+".insert", spectrumMatch);
    }
    
    public void saveSpectrumMatches(List<ProteinferSpectrumMatch> spectrumMatchList) {
        
        if(spectrumMatchList.size() == 0)
            return;
        StringBuilder buf = new StringBuilder();
        for(ProteinferSpectrumMatch smatch: spectrumMatchList) {
            buf.append(",(");
            buf.append(smatch.getMsRunSearchResultId()+",");
            buf.append(smatch.getProteinferPeptideId()+",");
            buf.append(smatch.getFdr());
            buf.append(")");
        }
        buf.deleteCharAt(0);
        super.save(sqlMapNameSpace+".insertAll", buf.toString());
    }
    
    public List<ProteinferSpectrumMatch> getSpectrumMatchForPeptide(int pinferPeptideId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPeptide", pinferPeptideId);
    }
    
    public List<ProteinferSpectrumMatch> getSpectrumMatchForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPinferId", pinferId);
    }
    
    public List<Integer> getSpectrumMatchIdsForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferId", pinferId);
    }
    
    public ProteinferSpectrumMatch getSpectrumMatchForMsResult(int pinferId, int runSearchResultId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("msRunSearchResultId", runSearchResultId);
        return (ProteinferSpectrumMatch) super.queryForObject(sqlMapNameSpace+".selectMatchforMsResult", map);
    }
}
