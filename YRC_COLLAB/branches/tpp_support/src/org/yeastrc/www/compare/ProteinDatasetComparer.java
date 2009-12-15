/**
 * ProteinDatasetComparer.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.dataset.FilterableDataset;
import org.yeastrc.www.compare.util.CommonNameLookupUtil;

/**
 * 
 */
public class ProteinDatasetComparer {

    private static final Logger log = Logger.getLogger(ProteinDatasetComparer.class.getName());
    
    private static ProteinDatasetComparer instance;
    
    public static final int PARSIM_NONE = 0;
    public static final int PARSIM_ONE = 1;
    public static final int PARSIM_ALL = 2;
    
    private ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
    private IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
    private ProteinProphetProteinDAO ppProtDao = fact.getProteinProphetProteinDao();
    
    private ProteinDatasetComparer() {}
    
    public static ProteinDatasetComparer instance() {
        if(instance == null) 
            instance = new ProteinDatasetComparer();
        return instance;
    }
    
    public ProteinComparisonDataset compareDatasets(List<FilterableDataset> datasets, int parsimoniousCount) throws Exception {
        
        Map<Integer, ComparisonProtein> proteinMap = new HashMap<Integer, ComparisonProtein>();
        
        boolean parsimoniousOnly = !(parsimoniousCount == PARSIM_NONE);
        
        // If parsimoniousCount == PARSIM_NONE this will get all the proteins that pass through the filter criteria
        // If parsimoniousCount == PARSIM_ONE || parsimoniousCount == PARSIM_AL this will get only parsimonious proteins
        // that pass through the filter criteria
        for(FilterableDataset dataset: datasets) {
            
            List<Integer> nrseqProteinIds = new ArrayList<Integer>(0);
            
            if(dataset.getSource() != DatasetSource.DTA_SELECT)
                nrseqProteinIds = getProteinIdsForDataset(dataset, parsimoniousOnly);
            
            for(int nrseqId: nrseqProteinIds) {
                ComparisonProtein protein = proteinMap.get(nrseqId);
                if(protein == null) {
                    protein = new ComparisonProtein(nrseqId);
                    proteinMap.put(nrseqId, protein);
                }
                DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                dpi.setParsimonious(true);
                dpi.setPresent(true);
                protein.addDatasetInformation(dpi);
            }
        }
        
        // If we want proteins that are parsimonious in at least one but not all the datasets we get
        // the list of non-parsimonious proteins in each dataset and keep the ones we have already seen
        // If a proteins was parsimonious in one of the datasets it should already be in the map.
        if(parsimoniousCount == PARSIM_ONE) {
            
            for(Dataset dataset: datasets) {
                
                List<Integer> nrseqProteinIds = getAllNonParsimoniousProteinIdsForDataset(dataset); // get only non-parsimonious
                
                for(int nrseqId: nrseqProteinIds) {
                    ComparisonProtein protein = proteinMap.get(nrseqId);
                    // This protein will not be in the map if: 
                    // 1. It did not make it through the filtering criteria in the previous step
                    // 2. It was not parsimonious in any of the datasets being compared. 
                    // Ignore this protein in such cases.
                    if(protein == null) {
                        continue; 
                    }
                    DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                    dpi.setParsimonious(false);
                    dpi.setPresent(true);
                    protein.addDatasetInformation(dpi);
                }
            }
        }
        
        ProteinComparisonDataset comparison = new ProteinComparisonDataset();
        comparison.setDatasets(datasets);
        for(ComparisonProtein protein: proteinMap.values())
            comparison.addProtein(protein);
        
        return comparison;
        
    }
    

    private List<Integer> getProteinIdsForDataset(FilterableDataset dataset, boolean parsimoniousOnly) {
        
        boolean getParsimonious = true;
        boolean getNonParsimonious = !parsimoniousOnly;
        
        if(dataset.getSource() == DatasetSource.PROTINFER) {
            if(dataset.getProteinFilterCrteria() == null)
                return protDao.getNrseqProteinIds(dataset.getDatasetId(), getParsimonious, getNonParsimonious);
            else {
                ProteinFilterCriteria filterCriteria = dataset.getProteinFilterCrteria();
                boolean oldParsimonious = filterCriteria.getParsimonious();
                boolean oldNonParsimonious = filterCriteria.getNonParsimonious();
                
                filterCriteria.setParsimonious(getParsimonious);
                filterCriteria.setNonParsimonious(getNonParsimonious);
                
                List<Integer> ids = protDao.getFilteredNrseqIds(dataset.getDatasetId(), 
                        filterCriteria);
                
                filterCriteria.setParsimonious(oldParsimonious);
                filterCriteria.setNonParsimonious(oldNonParsimonious);
                return ids;
                
            }
        }
        else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            if(dataset.getProteinFilterCrteria() == null)
                return ppProtDao.getNrseqProteinIds(dataset.getDatasetId(), getParsimonious, getNonParsimonious);
            else {
                ProteinFilterCriteria filterCriteria = dataset.getProteinFilterCrteria();
                boolean oldParsimonious = filterCriteria.getParsimonious();
                boolean oldNonParsimonious = filterCriteria.getNonParsimonious();
                
                filterCriteria.setParsimonious(getParsimonious);
                filterCriteria.setNonParsimonious(getNonParsimonious);
                
                List<Integer> ids = ppProtDao.getFilteredNrseqIds(dataset.getDatasetId(), 
                        (ProteinProphetFilterCriteria) dataset.getProteinFilterCrteria());
                
                filterCriteria.setParsimonious(oldParsimonious);
                filterCriteria.setNonParsimonious(oldNonParsimonious);
                return ids;
            }
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }
    
    private List<Integer> getAllNonParsimoniousProteinIdsForDataset(Dataset dataset) {
        
        if(dataset.getSource() == DatasetSource.PROTINFER) {
            return protDao.getNrseqProteinIds(dataset.getDatasetId(), false, true); // non-parsimonious only
        }
        else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            return ppProtDao.getNrseqProteinIds(dataset.getDatasetId(), false, true); // non-parsimonious only
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }

    
    public void applyFilters(ProteinComparisonDataset dataset, ProteinDatasetComparisonFilters filters) {
        
        List<ComparisonProtein> proteins = dataset.getProteins();
        // Apply the AND filters
        applyAndFilter(proteins, filters.getAndFilters());
        
        // Apply the OR filters
        applyOrFilter(proteins, filters.getOrFilters());
        
        // Apply the NOT filters
        applyNotFilter(proteins, filters.getNotFilters());
        
        // Apply the XOR filters
        applyXorFilter(proteins, filters.getXorFilters());
        
    }
    
    private void applyAndFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            for(Dataset dataset: datasets) {
                if(!protein.isInDataset(dataset)) {
                    iter.remove();
                    break;
                }
            }
        }
    }
    
    private void applyOrFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            boolean reject = true;
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    reject = false;
                    break;
                }
            }
            if(reject)  iter.remove();
        }
    }
    
    private void applyNotFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    iter.remove();
                    break;
                }
            }
        }
    }
    
    private void applyXorFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            int numOccur = 0;
            boolean reject = false;
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    numOccur++;
                    if(numOccur > 1) {
                        reject = true;
                        break;
                    }
                }
            }
            if(reject)  iter.remove();
        }
    }

    public void applySearchNameFilter(ProteinComparisonDataset dataset, String searchString) throws SQLException {
        
        if(searchString == null || searchString.trim().length() == 0)
            return;
        
        // get the protein ids for the names the user is searching for
        Set<Integer> proteinIds = new HashSet<Integer>();
        String tokens[] = searchString.split(",");
        
        List<String> notFound = new ArrayList<String>();
        
        // Do a common name lookup first
        for(String token: tokens) {
            String name = token.trim();
            if(name.length() > 0) {
                List<Integer> ids = CommonNameLookupUtil.getInstance().getProteinIds(name);
                proteinIds.addAll(ids);
                
                if(ids.size() == 0) {
                    notFound.add(name);
                }
            }
        }
        
        // Now look at the accession strings in tblProteinDatabase;
        if(notFound.size() > 0) {
            for(String name: notFound) {
                List<Integer> ids = NrSeqLookupUtil.getProteinIdsForAccession(name);
                if(ids != null)
                    proteinIds.addAll(ids);
            }
        }
        
        // sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        
        // Remove the ones that do not match
        Iterator<ComparisonProtein> iter = dataset.getProteins().iterator();
        while(iter.hasNext()) {
            ComparisonProtein prot = iter.next();
            if(Collections.binarySearch(sortedIds, prot.getNrseqId()) < 0)
                iter.remove();
        }
    }
    
    
    public void applyDescriptionFilter(ProteinComparisonDataset dataset, String searchString) throws SQLException {
        
        if(searchString == null || searchString.trim().length() == 0)
            return;
        
        // get the protein ids for the descriptions the user is searching for
        Set<Integer> proteinIds = new HashSet<Integer>();
        String tokens[] = searchString.split(",");
        
        List<String> notFound = new ArrayList<String>();
        
        for(String token: tokens) {
            String description = token.trim();
            if(description.length() > 0) {
                List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteinsForDescription(dataset.getFastaDatabaseIds(), description);
                for(NrDbProtein protein: proteins)
                    proteinIds.add(protein.getProteinId());
            }
        }
        
        // sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        
        // Remove the ones that do not match
        Iterator<ComparisonProtein> iter = dataset.getProteins().iterator();
        while(iter.hasNext()) {
            ComparisonProtein prot = iter.next();
            if(Collections.binarySearch(sortedIds, prot.getNrseqId()) < 0)
                iter.remove();
        }
    }
    
    /**
     * Find the number of common ids in two sorted lists of integers
     * @param list1
     * @param list2
     * @return
     */
    private static int commonIds(List<Integer> list1, List<Integer> list2) {
        
        if(list1 == null || list1.size() ==0)
            return 0;
        if(list2 == null || list2.size() == 0)
            return 0;
        
        int commonNum = 0;
        for(int id: list1) {
            if(Collections.binarySearch(list2, id) >= 0)
                commonNum++;
        }
        return commonNum;
    }
    
    /**
     * Find the number of common ids in three sorted lists of integers
     * @param list1
     * @param list2
     * @return
     */
    private static int commonIds(List<Integer> list1, List<Integer> list2, List<Integer> list3) {
        
        if(list1 == null || list1.size() ==0)
            return 0;
        if(list2 == null || list2.size() == 0)
            return 0;
        if(list3 == null || list3.size() == 0)
            return 0;
        
        int commonNum = 0;
        for(int id: list1) {
            if((Collections.binarySearch(list2, id) >= 0) && (Collections.binarySearch(list3, id) >= 0))
                commonNum++;
        }
        return commonNum;
    }
}
