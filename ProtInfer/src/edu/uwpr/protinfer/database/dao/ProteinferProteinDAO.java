package edu.uwpr.protinfer.database.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferCluster;
import edu.uwpr.protinfer.database.dto.ProteinferPeptideGroup;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferProteinGroup;

public class ProteinferProteinDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferProtein";

    
    public ProteinferProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveProteinferProtein(ProteinferProtein protein) {
        super.save(sqlMapNameSpace+".insert", protein);
    }
    
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("annotation", annotation);
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserAnnotation", map);
    }
    
    public void updateUserValidation(int pinferProteinId, ProteinUserValidation validation) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userValidation", validation.getStatusChar());
        map.put("pinferProteinId", pinferProteinId);
        super.update(sqlMapNameSpace+".updateUserValidation", map);
    }
    
    public ProteinferProtein getProteinferProtein(int pinferId, int nrseqProtId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("nrseqProtId", nrseqProtId);
        return (ProteinferProtein) super.queryForObject(sqlMapNameSpace+".select", map);
    }
    
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> getProteinferProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferProtein> getProteinferClusterProteins(int pinferId,int clusterId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterId", clusterId);
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRunCluster", map);
    }
    
    public List<ProteinferProtein> getProteinferGroupProteins(int pinferId,int groupId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return queryForList(sqlMapNameSpace+".selectProteinsForProteinferRunGroup", map);
    }
    
    private List<Integer> getMatchingPeptGroupIds(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return super.queryForList(sqlMapNameSpace+".selectPeptGrpIdsForProtGrpId", map);
    }
    
    public ProteinferProteinGroup getProteinferProteinGroup(int pinferId, int groupId) {
        List<ProteinferProtein> grpPeptides = getProteinferGroupProteins(pinferId, groupId);
        ProteinferProteinGroup group = new ProteinferProteinGroup(groupId);
        group.setProteins(grpPeptides);
        List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, groupId);
        ProteinferPeptideDAO peptDao = DAOFactory.instance().getProteinferPeptideDao();
        for(Integer peptGrpId: matchingPeptGrpIds) {
            ProteinferPeptideGroup peptGrp = peptDao.getProteinferPeptideGroup(pinferId, peptGrpId);
            group.addMatchingPeptideGroup(peptGrp);
        }
        return group;
    }
    
    public List<ProteinferProteinGroup> getProteinferProteinGroups(int pinferId) {
        // get all the proteins
        List<ProteinferProtein> allProteins = this.getProteinferProteins(pinferId);
        // sort by groupID
        Collections.sort(allProteins, new Comparator<ProteinferProtein>() {
            public int compare(ProteinferProtein o1, ProteinferProtein o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        ProteinferPeptideDAO peptDao = DAOFactory.instance().getProteinferPeptideDao();
        
        List<ProteinferProteinGroup> groups = new ArrayList<ProteinferProteinGroup>(allProteins.get(allProteins.size() - 1).getGroupId());
        int lastGrpId = -1;
        ProteinferProteinGroup lastGrp = null;
        for(ProteinferProtein protein: allProteins) {
            if(protein.getGroupId() != lastGrpId) {
                if(lastGrp != null) {
                    // all all the peptide groups for this protein group
                    List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
                    for(Integer peptGrpId: matchingPeptGrpIds) {
                        ProteinferPeptideGroup peptGrp = peptDao.getProteinferPeptideGroup(pinferId, peptGrpId);
                        lastGrp.addMatchingPeptideGroup(peptGrp);
                    }
                    // add this to the list of groups
                    groups.add(lastGrp);
                }
                lastGrp = new ProteinferProteinGroup(protein.getGroupId());
                lastGrpId = protein.getGroupId();
            }
            lastGrp.addProtein(protein);
        }
        // add the last one
        if(lastGrp != null) {
            // all all the peptide groups for this protein group
            List<Integer> matchingPeptGrpIds = getMatchingPeptGroupIds(pinferId, lastGrpId);
            for(Integer peptGrpId: matchingPeptGrpIds) {
                ProteinferPeptideGroup peptGrp = peptDao.getProteinferPeptideGroup(pinferId, peptGrpId);
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
    
    public ProteinferCluster getProteinferCluster(int pinferId, int clusterId) {
        ProteinferCluster cluster = new ProteinferCluster(pinferId, clusterId);
        List<Integer> protGrpIds = getGroupIdsForCluster(pinferId, clusterId);
        Set<Integer> uniqPeptGrpIds = new HashSet<Integer>();
        for(Integer protGrpId: protGrpIds) {
            ProteinferProteinGroup protGrp = getProteinferProteinGroup(pinferId, protGrpId);
            cluster.addProteinGroup(protGrp);
            for(ProteinferPeptideGroup peptGrp: protGrp.getMatchingPeptideGroups()) {
                if(!uniqPeptGrpIds.contains(peptGrp.getGroupId())) {
                    uniqPeptGrpIds.add(peptGrp.getGroupId());
                    cluster.addPeptideGroup(peptGrp);
                }
            }
        }
        Collections.sort(cluster.getProteinGroups(), new Comparator<ProteinferProteinGroup>() {
            public int compare(ProteinferProteinGroup o1, ProteinferProteinGroup o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        Collections.sort(cluster.getPeptideGroups(), new Comparator<ProteinferPeptideGroup>() {
            public int compare(ProteinferPeptideGroup o1, ProteinferPeptideGroup o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        return cluster;
    }
    
    public int getFilteredProteinCount(int proteinferId) {
       return (Integer) queryForObject(sqlMapNameSpace+".selectProteinCountForProteinferRun", proteinferId); 
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
    }
    
    public void delete(int pinferProteinId) {
        super.delete(sqlMapNameSpace+".delete", pinferProteinId);
    }
    
    /**
     * Type handler for converting between ProteinUserValidation and SQL's CHAR type.
     */
    public static final class UserValidationTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToUserValidation(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinUserValidation validation = (ProteinUserValidation) parameter;
            if (validation == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(validation.getStatusChar()));
        }

        public Object valueOf(String s) {
            return stringToUserValidation(s);
        }
        
        private ProteinUserValidation stringToUserValidation(String validationStr) {
            if (validationStr == null)
                return null;
            if (validationStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+validationStr+" to ProteinUserValidation");
            ProteinUserValidation userValidation = ProteinUserValidation.getStatusForChar(Character.valueOf(validationStr.charAt(0)));
            if (userValidation == null)
                throw new IllegalArgumentException("Invalid ProteinUserValidation value: "+validationStr);
            return userValidation;
        }
    }
}
