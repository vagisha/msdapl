package edu.uwpr.protinfer.database.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinferPeptide;

public class ProteinferPeptideDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferPeptide";
    
    public ProteinferPeptideDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveProteinferPeptide(int pinferProteinId, ProteinferPeptide peptide) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", peptide);
    }
    
    public void saveProteinferPeptideProteinMatch(int nrseqProteinId, int pinferPeptideId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("nrseqProteinId", nrseqProteinId);
        map.put("pinferPeptideId", pinferPeptideId);
        super.save(sqlMapNameSpace+".insertPeptideProteinMatch", map);
    }
    
    public List<Integer> getProteinferPeptideIdsForProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProtein", pinferProteinId);
    }
    
    public List<ProteinferPeptide> getProteinferPeptidesForProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<Integer> getProteinferPeptideIdsForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferPeptide> getProteinferPeptidesForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProteinferRun", proteinferId);
    }
    
    public ProteinferPeptide getProteinferPeptide(int pinferPeptideId) {
        return (ProteinferPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public void deleteProteinferPeptide(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
