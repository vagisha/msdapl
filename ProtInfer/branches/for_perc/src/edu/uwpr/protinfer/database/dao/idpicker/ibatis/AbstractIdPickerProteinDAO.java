package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
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
    
    public int update(GenericProteinferProtein<?> protein) {
        return protDao.update(protein);
    }
    
    public int updateIdPickerProtein(GenericIdPickerProtein<?> protein) {
        int updated = update(protein);
        if(updated > 0)
            return update(sqlMapNameSpace+".updateIdPickerProtein", protein);
        return 0;
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
    
    public List<Integer> getClusterIds(int pinferId) {
       return queryForList(sqlMapNameSpace+".selectClusterIdsForPinfer", pinferId); 
    }
    
    @Override
    public ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId) {
        return protDao.loadProtein(proteinferId, nrseqProteinId);
    }
    
//    @Override
//    public List<ProteinferProtein> loadProteinsN(int pinferId) {
//        return protDao.loadProteinsN(pinferId);
//    }
    
    public List<Integer> getProteinIdsForNrseqIds(int proteinferId, ArrayList<Integer> nrseqIds) {
        return protDao.getProteinIdsForNrseqIds(proteinferId, nrseqIds);
    }
    
    public int getFilteredParsimoniousProteinCount(int proteinferId) {
        return (Integer) queryForObject(sqlMapNameSpace+".selectParsimProteinCountForProteinferRun", proteinferId); 
    }
    
    @Override
    public void delete(int pinferProteinId) {
        protDao.delete(pinferProteinId);
    }

    @Override
    public int getProteinCount(int proteinferId) {
        return protDao.getProteinCount(proteinferId);
    }

    @Override
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return protDao.getProteinferProteinIds(proteinferId);
    }
    
    public List<Integer> getIdPickerGroupProteinIds(int pinferId, int groupId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return queryForList(sqlMapNameSpace+".selectProteinIdsForGroup", map);
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
    
    public int getIdPickerGroupCount(int pinferId) {
        return (Integer)queryForObject(sqlMapNameSpace+".selectGroupCount", pinferId);
    }
    
    
    public List<Integer> sortProteinIdsByCoverage(int pinferId, boolean groupProteins) {
        return proteinIdsByCoverage(pinferId, 0.0, true, groupProteins);
    }
    
    public static class ProteinGroupCoverage {
        private int proteinId;
        private int proteinGroupId;
        private double coverage;
        private double maxGrpCoverage;
        public void setProteinId(int proteinId) {
            this.proteinId = proteinId;
        }
        public void setProteinGroupId(int proteinGroupId) {
            this.proteinGroupId = proteinGroupId;
        }
        public void setCoverage(double coverage) {
            this.coverage = coverage;
        }
    }
    
    private List<Integer> proteinIdsByCoverage(int pinferId, double minCoverage, boolean sort, boolean groupProteins) {
        
        Map<String, Number> map = new HashMap<String, Number>(6);
        map.put("pinferId", pinferId);
        map.put("coverage", minCoverage);
        
        if(!groupProteins || (groupProteins && !sort)) {
            if(sort)    map.put("sort", 1);
            return queryForList(sqlMapNameSpace+".filterByCoverage", map);
        }
        else { // group proteins and sort
            // List of protein IDs along with their groupID and coverage (sorted by group, then coverage).
            List<ProteinGroupCoverage> prGrC = queryForList(sqlMapNameSpace+".filterProteinGroupCoverage", map);
            int lastGrp = -1;
            double lastMaxCoverage = 0.0;
            // Set the coverage for each protein in a group to be the max coverage in the group.
            for(ProteinGroupCoverage pgc: prGrC) {
                if(pgc.proteinGroupId != lastGrp) {
                    pgc.maxGrpCoverage = pgc.coverage;
                    lastGrp = pgc.proteinGroupId;
                    lastMaxCoverage = pgc.coverage; // first protein (for a group) has the max coverage.
                }
                else {
                    pgc.maxGrpCoverage = lastMaxCoverage;
                }
            }
            // Sort on coverage then protein group.
            Collections.sort(prGrC, new Comparator<ProteinGroupCoverage>() {
                @Override
                public int compare(ProteinGroupCoverage o1, ProteinGroupCoverage o2) {
                    int val = Double.valueOf(o1.maxGrpCoverage).compareTo(o2.maxGrpCoverage);
                    if(val != 0)    return val;
                    val = Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
                    if(val != 0)    return val;
                    return Double.valueOf(o1.coverage).compareTo(o2.coverage);
                }});
            List<Integer> proteinIds = new ArrayList<Integer>(prGrC.size());
            
            // return the list of protein IDs in the sorted order obove.
            for(ProteinGroupCoverage pgc: prGrC)
                proteinIds.add(pgc.proteinId);
            return proteinIds;
        }
    }
    
    public List<Integer> sortProteinsByNSAF(int pinferId, boolean groupProteins) {
        
        return proteinIdsByNSAF(pinferId, 0.0, true, groupProteins);
    }
    
    public static class ProteinGroupNsaf {
        private int proteinId;
        private int proteinGroupId;
        private double nsaf;
        private double maxNsaf;
        public void setProteinId(int proteinId) {
            this.proteinId = proteinId;
        }
        public void setProteinGroupId(int proteinGroupId) {
            this.proteinGroupId = proteinGroupId;
        }
        public void setNsaf(double nsaf) {
            this.nsaf = nsaf;
        }
    }
    
    private List<Integer> proteinIdsByNSAF(int pinferId, double minNsaf, boolean sort, boolean groupProteins) {
        
        Map<String, Number> map = new HashMap<String, Number>(6);
        map.put("pinferId", pinferId);
        map.put("nsaf", minNsaf);
        
        if (!groupProteins || (groupProteins && !sort)) {
            if(sort)    map.put("sort", 1);
            return queryForList(sqlMapNameSpace+".filterByNsaf", map);
        }
        else { // group proteins and sort
            // List of protein IDs along with their groupID and NSAF (sorted by group, then NSAF).
            List<ProteinGroupNsaf> prGrN = queryForList(sqlMapNameSpace+".filterProteinGroupNSAF", map);
            int lastGrp = -1;
            double lastMaxNsaf = 0.0;
            // Set the NSAF for each protein in a group to be the max NSAF in the group.
            for(ProteinGroupNsaf pgn: prGrN) {
                if(pgn.proteinGroupId != lastGrp) {
                    pgn.maxNsaf = pgn.nsaf;
                    lastGrp = pgn.proteinGroupId;
                    lastMaxNsaf = pgn.nsaf; // first protein (for a group) has the max nsaf.
                }
                else {
                    pgn.maxNsaf = lastMaxNsaf;
                }
            }
            // Sort on NSAF then protein group.
            Collections.sort(prGrN, new Comparator<ProteinGroupNsaf>() {
                @Override
                public int compare(ProteinGroupNsaf o1, ProteinGroupNsaf o2) {
                    int val = Double.valueOf(o1.nsaf).compareTo(o2.nsaf);
                    if(val != 0)    return val;
                    val = Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
                    if(val != 0)    return val;
                    return Double.valueOf(o1.maxNsaf).compareTo(o2.maxNsaf);
                }});
            List<Integer> proteinIds = new ArrayList<Integer>(prGrN.size());
            
            // return the list of protein IDs in the sorted order obove.
            for(ProteinGroupNsaf pgc: prGrN)
                proteinIds.add(pgc.proteinId);
            return proteinIds;
        }
    }
    
    public List<Integer> sortProteinIdsByValidationStatus(int pinferId) {
        return proteinIdsByValidationStatus(pinferId, new ArrayList<ProteinUserValidation>(0), true);
    }
    
    private List<Integer> proteinIdsByValidationStatus(int pinferId,
            List<ProteinUserValidation> validationStatus, boolean sort) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("pinferId", pinferId);
        if(validationStatus != null && validationStatus.size() > 0) {
            String vs = "";
            for(ProteinUserValidation v: validationStatus)
                vs += ",\'"+v.getStatusChar()+"\'";
            if(vs.length() > 0) vs = vs.substring(1); // remove first comma
            vs = "("+vs+")";
            map.put("validationStatus", vs);
        }
        if(sort)    map.put("sort", 1);
        return queryForList(sqlMapNameSpace+".filterByValidationStatus", map);
    }
    
    public List<Integer> sortProteinIdsBySpectrumCount(int pinferId, boolean groupProteins) {
        return proteinIdsBySpectrumCount(pinferId, 1, true, groupProteins);
    }
    
    private List<Integer> proteinIdsBySpectrumCount(int pinferId, int minSpecCount, boolean sort, boolean groupProteins) {
        Map<String, Number> map = new HashMap<String, Number>(6);
        map.put("pinferId", pinferId);
        map.put("numSpectra", minSpecCount);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        return queryForList(sqlMapNameSpace+".filterBySpecCount", map);
    }
    
    public List<Integer> sortProteinIdsByPeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins) {
        return proteinIdsByPeptideCount(pinferId, 1, peptideDef, true, groupProteins, false, false);
    }
    
    public List<Integer> sortProteinIdsByUniquePeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins) {
        return proteinIdsByPeptideCount(pinferId, 0, peptideDef, true, groupProteins, false, true);
    }
    
    private List<Integer> proteinIdsByUniquePeptideCount(int pinferId, int minUniqPeptideCount, PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious) {
        return proteinIdsByPeptideCount(pinferId, minUniqPeptideCount, peptDef, sort, groupProteins, isParsimonious, true);
    }
    
    private List<Integer> proteinIdsByAllPeptideCount(int pinferId, int minUniqPeptideCount, PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious) {
        return proteinIdsByPeptideCount(pinferId, minUniqPeptideCount, peptDef, sort, groupProteins, isParsimonious, false);
    }
    
    private List<Integer> proteinIdsByPeptideCount(int pinferId, int minPeptideCount, PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious, boolean uniqueToProtein) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        map.put("numPeptides", minPeptideCount);
        if(uniqueToProtein) map.put("uniqueToProtein", 1);
        if(isParsimonious)          map.put("isParsimonious", 1);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        
        List<Integer> peptideIds = null;
        // peptide uniquely defined by sequence
        if(!peptDef.isUseCharge() && !peptDef.isUseMods()) {
            peptideIds = queryForList(sqlMapNameSpace+".filterByPeptideCount_S", map);
        }
        // peptide uniquely defined by sequence, mods and charge
        if(peptDef.isUseCharge() && peptDef.isUseMods()) {
            peptideIds = queryForList(sqlMapNameSpace+".filterByPeptideCount_SMC", map);
        }
        // peptide uniquely defined by sequence and charge
        if(peptDef.isUseCharge() && !peptDef.isUseMods()) {
            peptideIds = peptideIdsByPeptideCount_SM_OR_SC(pinferId, minPeptideCount, 
                    sort, groupProteins, isParsimonious, uniqueToProtein, "charge");
        }
        // peptide uniquely defined by sequence and mods
        if(peptDef.isUseMods() && !peptDef.isUseCharge()) {
            peptideIds = peptideIdsByPeptideCount_SM_OR_SC(pinferId, minPeptideCount, 
                    sort, groupProteins, isParsimonious, uniqueToProtein, "modificationStateID");
        }
        
        // If we are looking for unique peptides the query will only return proteins with >= 1 unique peptides.
        // If our unique peptide count is 0 we need to add back the missing peptides to the end of the list
        if(uniqueToProtein && minPeptideCount == 0) {
            System.out.println("Adding more peptides");
            List<Integer> allPeptideIds = this.getIdPickerProteinIds(pinferId, isParsimonious, groupProteins);
            Set<Integer> found = new HashSet<Integer>((int) (allPeptideIds.size() * 1.5));
            found.addAll(peptideIds);
            int added = 0;
            for(int id: allPeptideIds) {
                if(!found.contains(id)) { 
                    added++;
                    peptideIds.add(0,id);
                }
            }
            System.out.println("Added: "+added);
        }
        return peptideIds;
    }
    
    private List<Integer> peptideIdsByPeptideCount_SM_OR_SC(int pinferId,
            int minPeptideCount, boolean sort, boolean groupProteins,
            boolean isParsimonious, boolean uniqueToProtein, String ionTableColumn) {
        
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // first create a temporary table
        String sql = "CREATE TEMPORARY TABLE ion_temp (piPeptideID INT UNSIGNED NOT NULL, piIonID INT UNSIGNED NOT NULL)";
        try {
           conn = super.getConnection();
           stmt  = conn.createStatement();
           stmt.executeUpdate(sql);
           
           stmt.close();
           
           stmt = conn.createStatement();
           sql = "INSERT INTO ion_temp "+
                  "(SELECT pept.id, ion.id FROM msProteinInferIon AS ion, msProteinInferPeptide AS pept "+
                  "WHERE pept.piRunID="+pinferId+
                  " AND pept.id = ion.piPeptideID GROUP BY pept.id, ion."+ionTableColumn+")";
           stmt.executeUpdate(sql);
           
           stmt.close();
           
           // add index on temp table
           sql = "ALTER TABLE ion_temp ADD INDEX (piPeptideID)";
           stmt = conn.createStatement();
           stmt.executeUpdate(sql);
           stmt.close();
           
           
           // no run the query we are interested in
           sql = prepareSql(pinferId, minPeptideCount, isParsimonious, 
                   uniqueToProtein, sort, groupProteins);
           pstmt = conn.prepareStatement(sql);
           pstmt.setInt(1, pinferId);
           pstmt.setInt(2, minPeptideCount);
           
           List<Integer> proteinIds = new ArrayList<Integer>();
           rs = pstmt.executeQuery();
           while(rs.next()) {
               proteinIds.add(rs.getInt("id"));
           }
           pstmt.close();
           
           // drop the temporary table
           stmt = conn.createStatement();
           sql = "DROP TABLE ion_temp";
           stmt.execute(sql);
           
           return proteinIds;
        }
        catch (SQLException e) {
            log.error("Failed in method peptideIdsByPeptideCount_SC_OR_SM", e);
            throw new RuntimeException("Failed in method peptideIdsByPeptideCount_SC_OR_SM", e);
        }
        finally {
             try {
                if(rs != null)      rs.close();
                if(stmt != null)    stmt.close();
                if(pstmt != null)   pstmt.close();
                if(conn != null)    conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
    }

    private String prepareSql(int pinferId, int minPeptideCount,
                boolean isParsimonious, boolean uniqueToProtein, boolean sort, boolean groupProteins) {
        String sql = "SELECT prot.id, count(*) AS cnt "+
                    "FROM msProteinInferProtein AS prot, "+ 
                    "IDPickerProtein as idpProt, "+
                    "msProteinInferProteinPeptideMatch AS m, "+ 
                    "msProteinInferPeptide AS pept, "+   
                    "ion_temp AS ion "+
                    "WHERE prot.id = idpProt.piProteinID "+
                    "AND idpProt.piProteinID = m.piProteinID "+
                    "AND m.piPeptideID = pept.id "+ 
                    "AND pept.id = ion.piPeptideID "+ 
                    "AND prot.piRunID = ? ";
        if(isParsimonious)
            sql += "AND idpProt.isParsimonious = 1 ";
        if(uniqueToProtein)
            sql += "AND pept.uniqueToProtein = 1 ";
        sql += "GROUP BY prot.id HAVING cnt >= ? ";
        if(sort) {
            sql += "ORDER BY cnt ";
            if(groupProteins) {
                sql += ", idpProt.groupID ";
            }
            sql += "DESC";
        }
       return sql;
    }
    
    

    private List<Integer> getIdPickerProteinIds(int pinferId, boolean isParsimonious, boolean groupProteins) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        if(isParsimonious)          map.put("isParsimonious", 1);
        if(groupProteins)           map.put("groupProteins", 1);
        return queryForList(sqlMapNameSpace+".idPickerProteinIds", map);
    }
    
    public List<Integer> getIdPickerProteinIds(int pinferId, boolean isParsimonious) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        if(isParsimonious)          map.put("isParsimonious", 1);
        return queryForList(sqlMapNameSpace+".idPickerProteinIds", map);
    }
    
    /**
     * Returns a map of proteinIds and proteinGroupIds.
     * Keys in the map are proteinIds and Values are proteinGroupIds
     */
    public Map<Integer, Integer> getProteinGroupIds(int pinferId, boolean parsimonious) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        if(parsimonious)          map.put("isParsimonious", 1);
        List<ProteinAndGroup> protGrps = queryForList(sqlMapNameSpace+".selectProteinAndGroupIds", map);
        
        Map<Integer, Integer> protGrpmap = new HashMap<Integer, Integer>((int) (protGrps.size() * 1.5));
        for(ProteinAndGroup pg: protGrps) {
            protGrpmap.put(pg.proteinId, pg.groupId);
        }
        return protGrpmap;
    }
    
    public static final class ProteinAndGroup {
        private int proteinId;
        private int groupId;
        
        public void setProteinId(int proteinId) {
            this.proteinId = proteinId;
        }
        
        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }
        
    }
    
    public List<Integer> sortProteinIdsByCluster(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinIdsByClusterId", pinferId);
    }

    public List<Integer> sortProteinIdsByGroup(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinIdsByGroupId", pinferId);
    }
    
    @Override
    public List<Integer> getFilteredSortedProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
        
        // Get a list of protein ids filtered by sequence coverage
        boolean sort = filterCriteria.getSortBy() == SORT_BY.COVERAGE;
        List<Integer> ids_cov = proteinIdsByCoverage(pinferId, filterCriteria.getCoverage(), sort, filterCriteria.isGroupProteins());
        
        // Get a list of protein ids filtered by spectrum count
        sort = filterCriteria.getSortBy() == SORT_BY.NUM_SPECTRA;
        List<Integer> ids_spec_count = proteinIdsBySpectrumCount(pinferId, filterCriteria.getNumSpectra(), sort, filterCriteria.isGroupProteins());
        
        // Get a list of protein ids filtered by peptide count
        sort = filterCriteria.getSortBy() == SORT_BY.NUM_PEPT;
        List<Integer> ids_pept = proteinIdsByAllPeptideCount(pinferId, filterCriteria.getNumPeptides(), 
                                            filterCriteria.getPeptideDefinition(),
                                            sort, 
                                            filterCriteria.isGroupProteins(), 
                                            filterCriteria.showParsimonious()
                                            );
        
        // Get a list of protein ids filtered by unique peptide count
        List<Integer> ids_uniq_pept = null;
        if(filterCriteria.getNumUniquePeptides() == 0 && filterCriteria.getSortBy() != SORT_BY.NUM_UNIQ_PEPT) {
            ids_uniq_pept = ids_pept;
        }
        else {
            sort = filterCriteria.getSortBy() == SORT_BY.NUM_UNIQ_PEPT;
            ids_uniq_pept = proteinIdsByUniquePeptideCount(pinferId, filterCriteria.getNumUniquePeptides(),
                                               filterCriteria.getPeptideDefinition(),
                                               sort,
                                               filterCriteria.isGroupProteins(),
                                               filterCriteria.showParsimonious());
        }
        
        
        // If the user is filtering on validation status 
        List<Integer> ids_validation_status = null;
        sort = filterCriteria.getSortBy() == SORT_BY.VALIDATION_STATUS;
        if(filterCriteria.getValidationStatus().size() > 0 || sort) {
            ids_validation_status = proteinIdsByValidationStatus(pinferId, filterCriteria.getValidationStatus(),
                                                                 sort);
        }
        
        // get the set of common ids; keep the order of ids returned from the query
        // that returned sorted results
        if(filterCriteria.getSortBy() == SORT_BY.COVERAGE) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status);
            return getCommonIds(ids_cov, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_SPECTRA) {
            Set<Integer> others = combineLists(ids_cov, ids_pept, ids_uniq_pept, ids_validation_status);
            return getCommonIds(ids_spec_count, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_PEPT) {
            Set<Integer> others = combineLists(ids_spec_count, ids_cov, ids_uniq_pept, ids_uniq_pept);
            return getCommonIds(ids_pept, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.NUM_UNIQ_PEPT) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_validation_status);
            return getCommonIds(ids_uniq_pept, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.GROUP_ID) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept, ids_validation_status);
            List<Integer> idsbyGroup = sortProteinIdsByGroup(pinferId);
            return getCommonIds(idsbyGroup, others);
        }
        else if (filterCriteria.getSortBy() == SORT_BY.CLUSTER_ID) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept, ids_validation_status);
            List<Integer> idsbyCluster = sortProteinIdsByCluster(pinferId);
            return getCommonIds(idsbyCluster, others);
        }
        else if(filterCriteria.getSortBy() == SORT_BY.VALIDATION_STATUS) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept);
            return getCommonIds(ids_validation_status, others);
        }
        else {
            Set<Integer> combineLists = combineLists(ids_cov, ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status);
            return new ArrayList<Integer>(combineLists);
        }
    }
    

    private final List<Integer> getCommonIds(List<Integer> ordered, Set<Integer> others) {
        Iterator<Integer> iter = ordered.iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            if(!others.contains(id))
                iter.remove();
        }
        return ordered;
    }
    
    private final Set<Integer> combineLists(List<Integer>...lists) {
        
        int numValidLists = 0;
        int count = 0;
        for(List<Integer> list: lists) {
            if(list == null)    continue;
            numValidLists++;
            count = Math.min(count, list.size());
        }
        Map<Integer, Integer> idCount = new HashMap<Integer, Integer>((int) (count*1.5));
        for(List<Integer> list: lists) {
            if(list == null)    continue;
            for(int id: list) {
                Integer c = idCount.get(id);
                if(c == null)   idCount.put(id, 1);
                else            idCount.put(id, ++c);
            }
        }
        Set<Integer> set = new HashSet<Integer>((int) (count*1.5));
        for(int id: idCount.keySet()) {
            if(idCount.get(id) == numValidLists)    set.add(id);
        }
        return set;
    }
}
