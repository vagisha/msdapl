package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;

public class ProteinferPeptideDAO extends BaseSqlMapDAO implements 
                    GenericProteinferPeptideDAO<ProteinferPeptide> {

    private static final String sqlMapNameSpace = "ProteinferPeptide";
    
    
    public ProteinferPeptideDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(GenericProteinferPeptide<?,?> peptide) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", peptide);
    }
    
    public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProtein", pinferProteinId);
    }
    
    public List<ProteinferPeptide> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferPeptide> loadPeptidesForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProteinferRun", proteinferId);
    }
    
    public ProteinferPeptide load(int pinferPeptideId) {
        return (ProteinferPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public void delete(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
