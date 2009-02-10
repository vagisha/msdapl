package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.Program;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.GenericProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferSpectrumMatchDAO extends BaseSqlMapDAO implements GenericProteinferSpectrumMatchDAO<ProteinferSpectrumMatch> {

    private static final String sqlMapNameSpace = "ProteinferSpectrumMatch";
    private final GenericProteinferRunDAO<?,?> runDao;

    public ProteinferSpectrumMatchDAO(SqlMapClient sqlMap, GenericProteinferRunDAO<?,?> runDao) {
        super(sqlMap);
        this.runDao = runDao;
    }

    public int saveSpectrumMatch(ProteinferSpectrumMatch spectrumMatch) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", spectrumMatch);
    }
    
    public ProteinferSpectrumMatch loadSpectrumMatch(int psmId) {
        return (ProteinferSpectrumMatch) queryForObject(sqlMapNameSpace+".select", psmId);
    }
    
    @Override
    public List<ProteinferSpectrumMatch> loadSpectrumMatchesForIon(int pinferIonId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForIon", pinferIonId);
    }
    
    @Override
    public ProteinferSpectrumMatch loadBestSpectrumMatchForIon(int pinferIonId) {
        return (ProteinferSpectrumMatch) queryForObject(sqlMapNameSpace+".selectBestMatchForIon", pinferIonId);
    }
    
    public List<ProteinferSpectrumMatch> loadSpectrumMatchesForPeptide(int pinferPeptideId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPeptide", pinferPeptideId);
    }
    
    public List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferId", pinferId);
    }
    
    public List<Integer> getSpectrumMatchIdsForPinferRunInput(int pinferId, int inputId) {
        
        // first determine if the inputID is a runSearchID or a runSearchAnalysisID
        GenericProteinferRun<?> pinferRun = runDao.loadProteinferRun(pinferId);
        Program inputGenerator = pinferRun.getInputGenerator();
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("inputId", inputId);
        if(Program.isSearchProgram(inputGenerator)) {
            return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferSearchInput", map);
        }
        else if(Program.isAnalysisProgram(inputGenerator)) {
            return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferAnalysisInput", map);
        }
        else
            return null;
    }

    public int update(ProteinferSpectrumMatch psm) {
       return update(sqlMapNameSpace+".update", psm);
    }
}
