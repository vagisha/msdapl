package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;

public class IdPickerPeptideDAO extends AbstractIdPickerPeptideDAO<IdPickerPeptide> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    

    public IdPickerPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap, peptDao);
    }
    
    public IdPickerPeptide load(int pinferPeptideId) {
        return (IdPickerPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public List<IdPickerPeptide> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<IdPickerPeptide> getIdPickerGroupPeptides(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForGroup", map);
    }
    
    
    
//    public IdPickerPeptideGroup getIdPickerPeptideGroup(int pinferId, int groupId) {
//        List<IdPickerPeptide> grpPeptides = getIdPickerGroupPeptides(pinferId, groupId);
//        IdPickerPeptideGroup group = new IdPickerPeptideGroup(groupId);
//        group.setPeptides(grpPeptides);
//        List<Integer> matchingProtGrpIds = getMatchingProtGroupIds(pinferId, groupId);
//        group.setMatchingProteinGroupIds(matchingProtGrpIds);
//        return group;
//    }
}
