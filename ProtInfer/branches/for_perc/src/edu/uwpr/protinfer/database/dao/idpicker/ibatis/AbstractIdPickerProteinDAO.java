package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.idpicker.GenericIdPickerProtein;

public abstract class AbstractIdPickerProteinDAO <P extends GenericIdPickerProtein<?>> 
    extends BaseSqlMapDAO
    implements GenericIdPickerProteinDAO<P> {

    private static final String sqlMapNameSpace = "IdPickerProtein";
    
    private final ProteinferProteinDAO protDao;
    
    public AbstractIdPickerProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap);
        this.protDao = protDao;
    }

    public int save(GenericProteinferProtein<?> protein) {
        return protDao.save(protein);
    }
    
    public int saveIdPickerProtein(GenericIdPickerProtein<?> protein) {
        int proteinId = save(protein);
        protein.setId(proteinId);
        save(sqlMapNameSpace+".insert", protein); // save entry in the IDPicker table
        return proteinId;
    }
    
    public boolean proteinPeptideGrpAssociationExists(int pinferId, int proteinGrpId, int peptideGrpId) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("pinferId", pinferId);
        map.put("protGrpId", proteinGrpId);
        map.put("peptGrpId", peptideGrpId);
        int count = (Integer)queryForObject(sqlMapNameSpace+".checkGroupAssociation", map);
        return count > 0;
    }
    
    public void saveProteinPeptideGroupAssociation(int pinferId, int proteinGrpId, int peptideGrpId) {
        
        if(proteinPeptideGrpAssociationExists(pinferId, proteinGrpId, peptideGrpId))
            return;
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("pinferId", pinferId);
        map.put("protGrpId", proteinGrpId);
        map.put("peptGrpId", peptideGrpId);
        save(sqlMapNameSpace+".insertGroupAssociation", map);
    }
    
    private List<Integer> getMatchingPeptGroupIds(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectPeptGrpIdsForProtGrpId", map);
    }
    
    public List<Integer> getGroupIdsForCluster(int pinferId, int clusterId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return super.queryForList(sqlMapNameSpace+".selectProtGrpIdsForClusterId", map);
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
    }
    
    @Override
    public void delete(int pinferProteinId) {
        protDao.delete(pinferProteinId);
    }

    @Override
    public int getFilteredProteinCount(int proteinferId) {
        return protDao.getFilteredProteinCount(proteinferId);
    }

    @Override
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return protDao.getProteinferProteinIds(proteinferId);
    }

    @Override
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        protDao.updateUserAnnotation(pinferProteinId, annotation);
    }

    @Override
    public void updateUserValidation(int pinferProteinId,
            ProteinUserValidation validation) {
        protDao.updateUserValidation(pinferProteinId, validation);
    }
    
    public void saveProteinferProteinPeptideMatch(int pinferProteinId,
            int pinferPeptideId) {
       protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
    }
}
