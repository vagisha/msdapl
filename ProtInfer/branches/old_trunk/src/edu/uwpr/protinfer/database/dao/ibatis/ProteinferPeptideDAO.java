package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferPeptideDAO extends BaseSqlMapDAO implements GenericProteinferPeptideDAO<ProteinferSpectrumMatch, ProteinferPeptide> {

    private static final String sqlMapNameSpace = "ProteinferPeptide";
    
    private final ProteinferSpectrumMatchDAO psmDao;
    
    public ProteinferPeptideDAO(SqlMapClient sqlMap, ProteinferSpectrumMatchDAO psmDao) {
        super(sqlMap);
        this.psmDao = psmDao;
    }

    public int save(BaseProteinferPeptide<?> peptide) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", peptide);
    }
    
    public int saveProteinferPeptide(ProteinferPeptide peptide) {
        int id = save(peptide);
        for(ProteinferSpectrumMatch psm: peptide.getSpectrumMatchList()) {
            psm.setProteinferPeptideId(id);
            psmDao.saveSpectrumMatch(psm);
        }
        return id;
    }
    
    public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProtein", pinferProteinId);
    }
    
    public List<ProteinferPeptide> getPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferPeptide> getPeptidesForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProteinferRun", proteinferId);
    }
    
    public ProteinferPeptide getPeptide(int pinferPeptideId) {
        return (ProteinferPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public void deleteProteinferPeptide(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
