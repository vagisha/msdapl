package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideBase;

public class IdPickerPeptideBaseDAO extends AbstractIdPickerPeptideDAO<IdPickerPeptideBase> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    
    public IdPickerPeptideBaseDAO(SqlMapClient sqlMap,
            ProteinferPeptideDAO peptDao) {
        super(sqlMap, peptDao);
    }

    @Override
    public IdPickerPeptideBase load(int pinferPeptideId) {
        return (IdPickerPeptideBase) super.queryForObject(sqlMapNameSpace+".selectBasePeptide", pinferPeptideId);
    }
    
    @Override
    public List<IdPickerPeptideBase> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectBasePeptidesForProtein", pinferProteinId);
    }
    
    @Override
    public List<IdPickerPeptideBase> loadIdPickerGroupPeptides(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectBasePeptidesForGroup", map);
    }
}
