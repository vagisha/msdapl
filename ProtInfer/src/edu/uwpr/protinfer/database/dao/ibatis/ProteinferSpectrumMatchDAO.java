package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferSpectrumMatchDAO extends BaseSqlMapDAO implements GenericProteinferSpectrumMatchDAO<ProteinferSpectrumMatch> {

    private static final String sqlMapNameSpace = "ProteinferSpectrumMatch";

    public ProteinferSpectrumMatchDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveSpectrumMatch(ProteinferSpectrumMatch spectrumMatch) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", spectrumMatch);
    }
    
    public ProteinferSpectrumMatch getSpectrumMatch(int psmId) {
        return (ProteinferSpectrumMatch) queryForObject(sqlMapNameSpace+".selectSpectrumMatch", psmId);
    }
    
    public List<ProteinferSpectrumMatch> getSpectrumMatchesForPeptide(int pinferPeptideId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPeptide", pinferPeptideId);
    }
    
    public List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferId", pinferId);
    }
    
    public List<Integer> getSpectrumMatchIdsForPinferRunAndRunSearch(int pinferId, int runSearchId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("runSearchId", runSearchId);
        return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferIdRunSearchId", map);
    }
}
