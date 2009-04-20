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
import org.yeastrc.bio.protein.ProteinNamerFactory;
import org.yeastrc.bio.protein.ProteinNamerUtils;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
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
            String systName = getSystematicName(nrseqProt);
            
//            String commonName = nrseqProt.getListing();
//            String description = nrseqProt.getDescription();
//          if(commonName.equals("UNKNOWN"))
//          commonName = "";
            
            CommonListing listing = CommonNameLookupUtil.instance().getCommonListing(nrseqProteinId);
            String commonName = listing.getName();
            String description = listing.getDescription();
            
            return new String[] {commonName, description, systName};
        }
        catch (Exception e) {
            log.error("Exception getting accession/description for protein Id: "+nrseqProteinId, e);
        }
        return null;
    }

    
    public static String getSystematicName(int nrseqProteinId)
        throws Exception {
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        NRProtein nrseqProt = (NRProtein)(nrpf.getProtein(nrseqProteinId));
        return getSystematicName(nrseqProt);
    }
    
    public static String getSystematicName(NRProtein nrseqProt)
            throws Exception {
        Set<String> systNames = nrseqProt.getSystematicNames();
        if(systNames == null || systNames.size() == 0)
            return "";
        
        StringBuilder buf = new StringBuilder();
        for(String sname: systNames) {
            buf.append(", ");
            buf.append(sname);
            if(buf.length() > 15)
                break;
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        
        if(buf.length() > 15) {
            buf.delete(15, buf.length());
            buf.append("...");
        }
        String systName = buf.toString();
        return systName;
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
                List<Integer> ids = CommonNameLookupUtil.instance().getProteinIds(name);
                proteinIds.addAll(ids);
                
                if(ids.size() == 0) {
                    notFound.add(name);
                }
            }
        }
        
        // No look at the accession strings in tblProteinDatabase;
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
