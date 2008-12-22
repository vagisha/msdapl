package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerPeptideDAO extends BaseSqlMapDAO implements GenericProteinferPeptideDAO<IdPickerSpectrumMatch, IdPickerPeptide> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    
    private final ProteinferPeptideDAO peptDao;
    private final IdPickerSpectrumMatchDAO idpPsmDao;

    public IdPickerPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao, IdPickerSpectrumMatchDAO idpPsmDao) {
        super(sqlMap);
        this.peptDao = peptDao;
        this.idpPsmDao = idpPsmDao;
    }
    
    public int save(BaseProteinferPeptide<?> peptide) {
       return peptDao.save(peptide); 
    }
    
    public int saveIdPickerPeptide(IdPickerPeptide peptide) {
        int id = save(peptide);
        peptide.setId(id);
        save(sqlMapNameSpace+".insert", peptide);
        
        // save the spectrum matches for this peptide
        for(IdPickerSpectrumMatch psm: peptide.getSpectrumMatchList()) {
            psm.setProteinferPeptideId(id);
            idpPsmDao.saveSpectrumMatch(psm);
        }
        return id;
    }
    
    public List<IdPickerPeptide> getPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<IdPickerPeptide> getPeptidesForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProteinferRun", proteinferId);
    }

    public IdPickerPeptide getPeptide(int pinferPeptideId) {
        return (IdPickerPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public List<IdPickerPeptide> getIdPickerGroupPeptides(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForGroup", map);
    }
    
    public List<Integer> getMatchingProtGroupIds(int pinferId, int peptideGroupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", peptideGroupId);
        return super.queryForList(sqlMapNameSpace+".selectProtGrpIdsForPeptGrpId", map);
    }
    
    public IdPickerPeptideGroup getIdPickerPeptideGroup(int pinferId, int groupId) {
        List<IdPickerPeptide> grpPeptides = getIdPickerGroupPeptides(pinferId, groupId);
        IdPickerPeptideGroup group = new IdPickerPeptideGroup(groupId);
        group.setPeptides(grpPeptides);
        List<Integer> matchingProtGrpIds = getMatchingProtGroupIds(pinferId, groupId);
        group.setMatchingProteinGroupIds(matchingProtGrpIds);
        return group;
    }

    public void deleteProteinferPeptide(int id) {
        peptDao.deleteProteinferPeptide(id);
    }

    public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
        return peptDao.getPeptideIdsForProteinferProtein(pinferProteinId);
    }
    
    public List<Integer> getPeptideIdsForProteinGroup(int idpProteinGroup) {
        return queryForList(sqlMapNameSpace+".selectPeptideIdsForProteinGroup", idpProteinGroup);
    }

    public List<Integer> getPeptideIdsForProteinferRun(
            int proteinferId) {
        return peptDao.getPeptideIdsForProteinferRun(proteinferId);
    }
}
