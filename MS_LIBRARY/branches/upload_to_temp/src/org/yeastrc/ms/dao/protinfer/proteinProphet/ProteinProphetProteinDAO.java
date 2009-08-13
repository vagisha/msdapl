/**
 * ProteinProphetProteinDAO.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

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
import org.yeastrc.ms.dao.protinfer.GenericProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria.SORT_BY;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinDAO extends BaseSqlMapDAO
    implements GenericProteinferProteinDAO<ProteinProphetProtein> {

    
    private static final String sqlMapNameSpace = "ProteinProphetProtein";
    
    private final ProteinferProteinDAO protDao;
    
    public ProteinProphetProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap);
        this.protDao = protDao;
    }

    @Override
    public int save(GenericProteinferProtein<?> protein) {
        return protDao.save(protein);
    }
    
    public int saveProteinProphetProtein(ProteinProphetProtein protein) {
        int proteinId = save(protein);
        protein.setId(proteinId);
        save(sqlMapNameSpace+".insert", protein); // save entry in the ProteinProphetProtein table
        return proteinId;
    }

    @Override
    public int update(GenericProteinferProtein<?> protein) {
        return protDao.update(protein);
    }
    
    @Override
    public void saveProteinferProteinPeptideMatch(int pinferProteinId,
            int pinferPeptideId) {
        protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
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
    
    @Override
    public void delete(int pinferProteinId) {
        protDao.delete(pinferProteinId);
    }

    @Override
    public List<Integer> getNrseqIdsForRun(int proteinferId) {
        return protDao.getNrseqIdsForRun(proteinferId);
    }

    @Override
    public int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds) {
        // TODO may need to override -- 
        // 1. check if any of these is a ProteinProphet run
        // 2. only return peptides that have at least one ion as "contributing_evidence"
        return protDao.getPeptideCountForProtein(nrseqId, pinferIds);
    }

    @Override
    public List<String> getPeptidesForProtein(int nrseqId,
            List<Integer> pinferIds) {
        // TODO may need to override -- 
        // 1. check if any of these is a ProteinProphet run
        // 2. only return peptides that have at least one ion as "contributing_evidence"
        return protDao.getPeptidesForProtein(nrseqId, pinferIds);
    }

    @Override
    public int getProteinCount(int proteinferId) {
        return protDao.getProteinCount(proteinferId);
    }

    @Override
    public List<Integer> getProteinIdsForNrseqIds(int proteinferId,
            ArrayList<Integer> nrseqIds) {
        return protDao.getProteinIdsForNrseqIds(proteinferId, nrseqIds);
    }

    @Override
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return protDao.getProteinferProteinIds(proteinferId);
    }

    public List<Integer> getProteinferProteinIds(int pinferId, boolean isParsimonious) {
        
        Map<String, Number> map = new HashMap<String, Number>(4);
        map.put("pinferId", pinferId);
        if(isParsimonious)          map.put("isSubsumed", 0);
        return queryForList(sqlMapNameSpace+".proteinProphetProteinIds", map);
    }
    
    public  int getIndistinguishableGroupCount(int pinferId, boolean parsimonious) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        if(parsimonious) {
            map.put("isSubsumed", 0);
        }
        return (Integer)queryForObject(sqlMapNameSpace+".selectGroupCount", map);
    }
    
    @Override
    public ProteinProphetProtein loadProtein(int pinferProteinId) {
        return (ProteinProphetProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }
    
    @Override
    public ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId) {
        return protDao.loadProtein(proteinferId, nrseqProteinId);
    }

    @Override
    public List<ProteinProphetProtein> loadProteins(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }
    
    
    public List<Integer> getFilteredSortedProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
        
        // Get a list of protein ids filtered by sequence coverage
        boolean sort = filterCriteria.getSortBy() == SORT_BY.COVERAGE;
        List<Integer> ids_cov = proteinIdsByCoverage(pinferId, 
                filterCriteria.getCoverage(), filterCriteria.getMaxCoverage(),
                sort, filterCriteria.isGroupProteins());
        
        // Get a list of protein ids filtered by spectrum count
        sort = filterCriteria.getSortBy() == SORT_BY.NUM_SPECTRA;
        List<Integer> ids_spec_count = proteinIdsBySpectrumCount(pinferId, 
                filterCriteria.getNumSpectra(), filterCriteria.getNumMaxSpectra(),
                sort, filterCriteria.isGroupProteins());
        
        // Get a list of protein ids filtered by peptide count
        sort = filterCriteria.getSortBy() == SORT_BY.NUM_PEPT;
        List<Integer> ids_pept = proteinIdsByAllPeptideCount(pinferId, 
                                            filterCriteria.getNumPeptides(), filterCriteria.getNumMaxPeptides(),
                                            filterCriteria.getPeptideDefinition(),
                                            sort, 
                                            filterCriteria.isGroupProteins(), 
                                            filterCriteria.showParsimonious()
                                            );
        
        // Get a list of protein ids filtered by UNIQUE peptide count
        List<Integer> ids_uniq_pept = null;
        if(filterCriteria.getNumUniquePeptides() == 0  &&
           filterCriteria.getNumMaxUniquePeptides() == Integer.MAX_VALUE
           && filterCriteria.getSortBy() != SORT_BY.NUM_UNIQ_PEPT) {
            ids_uniq_pept = ids_pept;
        }
        else {
            sort = filterCriteria.getSortBy() == SORT_BY.NUM_UNIQ_PEPT;
            ids_uniq_pept = proteinIdsByUniquePeptideCount(pinferId, 
                                               filterCriteria.getNumUniquePeptides(),
                                               filterCriteria.getNumMaxUniquePeptides(),
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
        else if(filterCriteria.getSortBy() == SORT_BY.VALIDATION_STATUS) {
            Set<Integer> others = combineLists(ids_spec_count, ids_pept, ids_cov, ids_uniq_pept);
            return getCommonIds(ids_validation_status, others);
        }
        else {
            Set<Integer> combineLists = combineLists(ids_cov, ids_spec_count, ids_pept, ids_uniq_pept, ids_validation_status);
            return new ArrayList<Integer>(combineLists);
        }
    }
    
    public List<Integer> sortProteinIdsByGroup(int pinferId) {
        return queryForList(sqlMapNameSpace+".proteinIdsByGroupId", pinferId);
    }
    
    // -----------------------------------------------------------------------------------------------
    // COVERAGE
    // -----------------------------------------------------------------------------------------------
    private List<Integer> proteinIdsByCoverage(int pinferId, 
            double minCoverage, double maxCoverage,
            boolean sort, boolean groupProteins) {
        
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minCoverage", minCoverage);
        map.put("maxCoverage", maxCoverage);
        
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
    
    // -----------------------------------------------------------------------------------------------
    // SPECTRUM COUNT
    // -----------------------------------------------------------------------------------------------
    public List<Integer> sortProteinIdsBySpectrumCount(int pinferId, boolean groupProteins) {
        return proteinIdsBySpectrumCount(pinferId, 1, Integer.MAX_VALUE, true, groupProteins);
    }
    
    private List<Integer> proteinIdsBySpectrumCount(int pinferId, int minSpecCount, int maxSpecCount,
            boolean sort, boolean groupProteins) {
        
        // If we are NOT filtering anything AND NOT sorting on spectrum count just return all the protein Ids
        // for this protein inference run
        if(minSpecCount <= 1 && maxSpecCount == Integer.MAX_VALUE && !sort) {
            return getProteinferProteinIds(pinferId, false);
        }
        
        Map<String, Number> map = new HashMap<String, Number>(10);
        map.put("pinferId", pinferId);
        map.put("minSpectra", minSpecCount);
        map.put("maxSpectra", maxSpecCount);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        return queryForList(sqlMapNameSpace+".filterBySpecCount", map);
    }
    
    // -----------------------------------------------------------------------------------------------
    // PEPTIDE COUNT
    // -----------------------------------------------------------------------------------------------
    private List<Integer> proteinIdsByUniquePeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount, 
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious) {
        return proteinIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount, 
                peptDef, sort, groupProteins, isParsimonious, true);
    }
    
    private List<Integer> proteinIdsByAllPeptideCount(int pinferId, 
            int minUniqPeptideCount, int maxUniqPeptideCount,
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious) {
        return proteinIdsByPeptideCount(pinferId, 
                minUniqPeptideCount, maxUniqPeptideCount,
                peptDef, sort, groupProteins, isParsimonious, false);
    }
    
    private List<Integer> proteinIdsByPeptideCount(int pinferId, int minPeptideCount, int maxPeptideCount,
            PeptideDefinition peptDef,
            boolean sort, boolean groupProteins, boolean isParsimonious, boolean uniqueToProtein) {
        
        // If we are NOT filtering anything AND NOT sorting on peptide count just return all the protein Ids
        // for this protein inference run
        if(!uniqueToProtein) {
            if(minPeptideCount <= 1 && maxPeptideCount == Integer.MAX_VALUE && !sort) {
                return getProteinferProteinIds(pinferId, isParsimonious);
            }
        }
        if(uniqueToProtein) {
            if(minPeptideCount <= 0 && maxPeptideCount == Integer.MAX_VALUE && !sort) {
                return getProteinferProteinIds(pinferId, isParsimonious);
            }
        }
        
        Map<String, Number> map = new HashMap<String, Number>(12);
        map.put("pinferId", pinferId);
        map.put("minPeptides", minPeptideCount);
        map.put("maxPeptides", maxPeptideCount);
        if(uniqueToProtein) map.put("uniqueToProtein", 1);
        if(isParsimonious)          map.put("isSubsumed", 0);
        if(sort)                    map.put("sort", 1);
        if(sort && groupProteins)   map.put("groupProteins", 1);
        
        List<Integer> peptideIds = null;
        // peptide uniquely defined by sequence, mods and charge
        if(peptDef.isUseCharge() && peptDef.isUseMods()) {
            peptideIds = queryForList(sqlMapNameSpace+".filterByPeptideCount_SMC", map);
        }
        
        return peptideIds;
    }
    
    
    // -----------------------------------------------------------------------------------------------
    // VALIDATION STATUS
    // -----------------------------------------------------------------------------------------------
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
    
    private final List<Integer> getCommonIds(List<Integer> ordered, Set<Integer> others) {
        Iterator<Integer> iter = ordered.iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            if(!others.contains(id))
                iter.remove();
        }
        return ordered;
    }

    public List<ProteinProphetProtein> loadProteinProphetGroupProteins(
            int pinferId, int groupId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("groupId", groupId);
        return queryForList(sqlMapNameSpace+".selectProteinsForGroup", map);
    }
    
    /**
     * Returns a map of proteinIds and proteinGroupIds.
     * Keys in the map are proteinIds and Values are proteinGroupIds
     */
    public Map<Integer, Integer> getProteinGroupIds(int pinferId, boolean parsimonious) {
        Map<String, Number> map = new HashMap<String, Number>(8);
        map.put("pinferId", pinferId);
        if(parsimonious)          map.put("isSubsumed", 0);
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
}
