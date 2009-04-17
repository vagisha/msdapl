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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;

/**
 * 
 */
public class ProteinDatasetComparer {

    private static final Logger log = Logger.getLogger(ProteinDatasetComparer.class.getName());
    
    private static ProteinDatasetComparer instance;
    
    private ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
    private IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
    
    private ProteinDatasetComparer() {}
    
    public static ProteinDatasetComparer instance() {
        if(instance == null) 
            instance = new ProteinDatasetComparer();
        return instance;
    }
    
    public ProteinComparisonDataset compareDatasets(List<Dataset> datasets, boolean parsimoniousOnly) throws Exception {
        
        Map<Integer, ComparisonProtein> proteinMap = new HashMap<Integer, ComparisonProtein>();
        
        // First get the parsimonious proteins
        for(Dataset dataset: datasets) {
            
            
            List<Integer> nrseqProteinIds = new ArrayList<Integer>(0);
            
            if(dataset.getSource() == DatasetSource.PROT_INFER)
                nrseqProteinIds = getProteinIdsForDataset(dataset, true, false); // get only parsimonious
            
            else if (dataset.getSource() == DatasetSource.DTA_SELECT) 
                nrseqProteinIds = getDtaSelectProteinIds(dataset);
                
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
        
        // now get the non-parsimonious proteins, if required (Protein Inference ONLY)
        if(!parsimoniousOnly) {
            
            for(Dataset dataset: datasets) {
                
                if(dataset.getSource() != DatasetSource.PROT_INFER)
                    continue;
                
                List<Integer> nrseqProteinIds = getProteinIdsForDataset(dataset, false, true); // get only non-parsimonious
                
                for(int nrseqId: nrseqProteinIds) {
                    ComparisonProtein protein = proteinMap.get(nrseqId);
                    if(protein == null) {
                        continue; // Ignore this proteins if it was not already listed as 
                                  // parsimonious for one or more of the datasets being compared.
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
    
    private List<Integer> getDtaSelectProteinIds(Dataset dataset) throws InvalidIDException, SQLException, Exception {
        YatesRun run = new YatesRun();
        run.load(dataset.getDatasetId());
        return run.getNrseqIds();
    }

    private List<Integer> getProteinIdsForDataset(Dataset dataset, boolean parsimonious, boolean nonParsimonious) {
        
        if(dataset.getSource() == DatasetSource.PROT_INFER) {
            return protDao.getNrseqProteinIds(dataset.getDatasetId(), parsimonious, nonParsimonious);
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }

    static String[] getProteinAccessionDescription(int nrseqProteinId, boolean fullLookup) {
        
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        NRProtein nrseqProt = null;
        try {
            nrseqProt = (NRProtein)(nrpf.getProtein(nrseqProteinId));
            String commonName = nrseqProt.getListing();
            String description = nrseqProt.getDescription();
            
//            String commonName = CommonNameLookupUtil.instance().getCommonNames(nrseqProteinId, fullLookup);
//           if(commonName.equals("UNKNOWN"))
//              commonName = "";
            
            return new String[] {commonName, description};
//            return new String[]{"commonName", "description"};
        }
        catch (Exception e) {
            log.error("Exception getting accession/description for protein Id: "+nrseqProteinId, e);
        }
        return null;
    }

    public void applyFilters(ProteinComparisonDataset dataset, ProteinDatasetComparisonFilters filters) {
        
        List<ComparisonProtein> proteins = dataset.getProteins();
        // Apply the AND filters
        applyAndFilter(proteins, filters.getAndFilters());
        
        // Apply the OR filters
        applyOrFilter(proteins, filters.getOrFilters());
        
        // Apply the NOT filters
        applyNotFilter(proteins, filters.getNotFilters());
        
    }
    
    private void applyAndFilter(List<ComparisonProtein> proteins, List<Dataset> datasets) {
        
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
    
    private void applyOrFilter(List<ComparisonProtein> proteins, List<Dataset> datasets) {
        
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
    
    private void applyNotFilter(List<ComparisonProtein> proteins, List<Dataset> datasets) {
        
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
