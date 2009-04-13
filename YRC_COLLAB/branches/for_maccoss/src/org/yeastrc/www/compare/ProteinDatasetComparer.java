/**
 * ProteinDatasetComparer.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.bio.protein.CommonNameLookupUtil;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

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
    
    public ComparisonDataset compareDatasets(List<Dataset> datasets, boolean parsimoniousOnly) {
        
        Map<Integer, ComparisonProtein> proteinMap = new HashMap<Integer, ComparisonProtein>();
        
        // first get the parsimonious proteins 
        for(Dataset dataset: datasets) {
            
            List<Integer> nrseqProteinIds = getProteinIdsForDataset(dataset, true, false); // get only parsimonious
            
            for(int nrseqId: nrseqProteinIds) {
                ComparisonProtein protein = proteinMap.get(nrseqId);
                if(protein == null) {
                    String[] nameDescr = getProteinAccessionDescription(nrseqId, false);
                    protein = new ComparisonProtein(nrseqId, nameDescr[0], nameDescr[1]);
                    proteinMap.put(nrseqId, protein);
                }
                DatasetProteinInformation dpi = new DatasetProteinInformation(dataset);
                dpi.setParsimonious(true);
                dpi.setPresent(true);
                protein.addDatasetInformation(dpi);
            }
        }
        
        // now get the non-parsimonious proteins, if required
        if(!parsimoniousOnly) {
            
            for(Dataset dataset: datasets) {
                
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
        
        ComparisonDataset comparison = new ComparisonDataset();
        comparison.setDatasets(datasets);
        for(ComparisonProtein protein: proteinMap.values())
            comparison.addProtein(protein);
        
        return comparison;
        
    }
    
    private List<Integer> getProteinIdsForDataset(Dataset dataset, boolean parsimonious, boolean nonParsimonious) {
        
        if(dataset.getSource() == DatasetSource.PROT_INFER) {
            return protDao.getNrseqProteinIds(dataset.getDatasetId(), parsimonious, nonParsimonious);
        }
        else {
            return new ArrayList<Integer>(0);
        }
    }

    private static String[] getProteinAccessionDescription(int nrseqProteinId, boolean fullLookup) {
        
//        NRProteinFactory nrpf = NRProteinFactory.getInstance();
//        NRProtein nrseqProt = null;
        try {
//            nrseqProt = (NRProtein)(nrpf.getProtein(nrseqProteinId));
//            String commonName = nrseqProt.getListing();
//            String description = nrseqProt.getDescription();
            
//            String commonName = CommonNameLookupUtil.instance().getCommonNames(nrseqProteinId, fullLookup);
//           if(commonName.equals("UNKNOWN"))
//              commonName = "";
            
//            return new String[] {commonName, description};
            return new String[]{"commonName", "description"};
        }
        catch (Exception e) {
            log.error("Exception getting accession/description for protein Id: "+nrseqProteinId, e);
        }
        return null;
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
