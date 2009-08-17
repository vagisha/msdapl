package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;

public class IdPickerProteinBaseDAO extends AbstractIdPickerProteinDAO<IdPickerProteinBase> {

private static final String sqlMapNameSpace = "IdPickerProtein";
    
    public IdPickerProteinBaseDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap, protDao);
    }

    public IdPickerProteinBase loadProtein(int pinferProteinId) {
        return (IdPickerProteinBase) super.queryForObject(sqlMapNameSpace+".selectBaseProtein", pinferProteinId);
    }
    
    public List<IdPickerProteinBase> loadProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForProteinferRun", proteinferId);
    }
    
    public List<IdPickerProteinBase> loadIdPickerClusterProteins(int pinferId,int clusterId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForCluster", map);
    }
    
    public List<IdPickerProteinBase> loadIdPickerGroupProteins(int pinferId,int groupId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForGroup", map);
    }
}
