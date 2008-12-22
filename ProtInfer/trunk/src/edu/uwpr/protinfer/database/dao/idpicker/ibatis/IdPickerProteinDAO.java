package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerCluster;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroupSummary;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerProteinDAO extends BaseSqlMapDAO 
                    implements GenericProteinferProteinDAO<IdPickerSpectrumMatch, IdPickerPeptide, IdPickerProtein> {

    private static final String sqlMapNameSpace = "IdPickerProtein";
    
    private final ProteinferProteinDAO protDao;
    private final IdPickerPeptideDAO idpPeptDao;
    
    public IdPickerProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao, IdPickerPeptideDAO idpPeptDao) {
        super(sqlMap);
        this.protDao = protDao;
        this.idpPeptDao = idpPeptDao;
    }

    public int save(BaseProteinferProtein<?, ?> protein) {
        return protDao.save(protein);
    }
    
    public int saveIdPickerProtein(IdPickerProtein protein) {
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
    
    public IdPickerProtein getProtein(int pinferProteinId) {
        return (IdPickerProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    public List<IdPickerProtein> getProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<IdPickerProtein> getIdPickerClusterProteins(int pinferId,int clusterId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return queryForList(sqlMapNameSpace+".selectProteinsForCluster", map);
    }
    
    public List<IdPickerProtein> getIdPickerGroupProteins(int pinferId,int groupId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return queryForList(sqlMapNameSpace+".selectProteinsForGroup", map);
    }
    
    private List<Integer> getMatchingPeptGroupIds(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectPeptGrpIdsForProtGrpId", map);
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
    }
    
    public IdPickerProteinGroupSummary getIdPickerProteinGroupSummary(int pinferId, int proteinGroupId) {
        List<IdPickerProtein> grpProteins = getIdPickerGroupProteins(pinferId, proteinGroupId);
        IdPickerProteinGroupSummary summary = new IdPickerProteinGroupSummary(proteinGroupId);
        if(grpProteins.size() == 0) 
            return summary;
        summary.setProteins(grpProteins);
        int numPeptides = idpPeptDao.getPeptideIdsForProteinferProtein(grpProteins.get(0).getId()).size();
        idpPeptDao.getP
        
    }
    
    public IdPickerProteinGroup getIdPickerProteinGroup(int pinferId, int groupId) {
        List<IdPickerProtein> grpProteins = getIdPickerGroupProteins(pinferId, groupId);
        IdPickerProteinGroup group = new IdPickerProteinGroup(groupId);
        group.setProteins(grpProteins);
        List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, groupId);
        for(Integer peptGrpId: matchingPeptGrpIds) {
            IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
            group.addMatchingPeptideGroup(peptGrp);
        }
        return group;
    }
    
    public List<IdPickerProteinGroup> getIdPickerProteinGroups(int pinferId) {
        // get all the proteins
        List<IdPickerProtein> allProteins = this.getProteins(pinferId);
        // sort by groupID
        Collections.sort(allProteins, new Comparator<IdPickerProtein>() {
            public int compare(IdPickerProtein o1, IdPickerProtein o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        
        List<IdPickerProteinGroup> groups = new ArrayList<IdPickerProteinGroup>(allProteins.get(allProteins.size() - 1).getGroupId());
        int lastGrpId = -1;
        IdPickerProteinGroup lastGrp = null;
        for(IdPickerProtein protein: allProteins) {
            if(protein.getGroupId() != lastGrpId) {
                if(lastGrp != null) {
                    // all all the peptide groups for this protein group
                    List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
                    for(Integer peptGrpId: matchingPeptGrpIds) {
                        IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
                        lastGrp.addMatchingPeptideGroup(peptGrp);
                    }
                    // add this to the list of groups
                    groups.add(lastGrp);
                }
                lastGrp = new IdPickerProteinGroup(protein.getGroupId());
                lastGrpId = protein.getGroupId();
            }
            lastGrp.addProtein(protein);
        }
        // add the last one
        if(lastGrp != null) {
            // all all the peptide groups for this protein group
            List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
            for(Integer peptGrpId: matchingPeptGrpIds) {
                IdPickerPeptideGroup peptGrp = idpPeptDao.getIdPickerPeptideGroup(pinferId, peptGrpId);
                lastGrp.addMatchingPeptideGroup(peptGrp);
            }
            // add this to the list of groups
            groups.add(lastGrp);
        }
        
        return groups;
    }
    
    public List<Integer> getGroupIdsForCluster(int pinferId, int clusterId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return super.queryForList(sqlMapNameSpace+".selectProtGrpIdsForClusterId", map);
    }
    
    public IdPickerCluster getIdPickerCluster(int pinferId, int clusterId) {
        IdPickerCluster cluster = new IdPickerCluster(pinferId, clusterId);
        List<Integer> protGrpIds = getGroupIdsForCluster(pinferId, clusterId);
        Set<Integer> uniqPeptGrpIds = new HashSet<Integer>();
        for(Integer protGrpId: protGrpIds) {
            IdPickerProteinGroup protGrp = getIdPickerProteinGroup(pinferId, protGrpId);
            cluster.addProteinGroup(protGrp);
            for(IdPickerPeptideGroup peptGrp: protGrp.getMatchingPeptideGroups()) {
                if(!uniqPeptGrpIds.contains(peptGrp.getGroupId())) {
                    uniqPeptGrpIds.add(peptGrp.getGroupId());
                    cluster.addPeptideGroup(peptGrp);
                }
            }
        }
        Collections.sort(cluster.getProteinGroups(), new Comparator<IdPickerProteinGroup>() {
            public int compare(IdPickerProteinGroup o1, IdPickerProteinGroup o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        Collections.sort(cluster.getPeptideGroups(), new Comparator<IdPickerPeptideGroup>() {
            public int compare(IdPickerPeptideGroup o1, IdPickerPeptideGroup o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        return cluster;
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
