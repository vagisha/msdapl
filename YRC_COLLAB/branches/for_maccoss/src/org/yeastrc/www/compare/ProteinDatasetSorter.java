/**
 * ProteinDatasetSorter.java
 * @author Vagisha Sharma
 * Apr 18, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

import edu.uwpr.protinfer.util.ProteinUtils;

/**
 * 
 */
public class ProteinDatasetSorter {

    private static ProteinDatasetSorter instance;
    private ProteinDatasetSorter() {}
    
    public static ProteinDatasetSorter instance() {
        if(instance == null)
            instance = new ProteinDatasetSorter();
        return instance;
    }
    
//    public void sortByName(ProteinComparisonDataset dataset) throws SQLException {
//        List<ComparisonProtein> proteins = dataset.getProteins();
//        
//        List<Integer> nrseqProteinIds = new ArrayList<Integer>(proteins.size());
//        for(ComparisonProtein protein: proteins)
//            nrseqProteinIds.add(protein.getNrseqId());
//        
//        CommonNameLookupUtil lookup = CommonNameLookupUtil.getInstance();
//        List<ProteinListing> listings = lookup.getCommonListings(nrseqProteinIds);
//        for(int i = 0; i < nrseqProteinIds.size(); i++) {
//            ComparisonProtein protein = proteins.get(i);
//            ProteinListing listing = listings.get(i);
//            protein.setCommonName(listing.getName());
//            protein.setDescription(listing.getDescription());
//        }
//        
//        Collections.sort(proteins, new NameCompartor());
//    }
    
    public void sortByPeptideCount(ProteinComparisonDataset dataset, SORT_ORDER sortOrder) throws SQLException {
        List<ComparisonProtein> proteins = dataset.getProteins();
        for(ComparisonProtein protein: proteins) {
         // get the (max)number of peptides identified for this protein
            protein.setMaxPeptideCount(DatasetPeptideComparer.instance().getMaxPeptidesForProtein(protein));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new PeptideCountCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new PeptideCountCompartorAsc());
    }
    
    public void sortByMolecularWeight(ProteinComparisonDataset dataset, SORT_ORDER sortOrder) {
        
        List<ComparisonProtein> proteins = dataset.getProteins();
        sortByMolecularWeight(sortOrder, proteins);
    }

    private void sortByMolecularWeight(SORT_ORDER sortOrder, List<ComparisonProtein> proteins) {
        for(ComparisonProtein protein: proteins) {
            if(protein.molWtAndPiSet())
                continue;
            // get the protein properties
            String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
            protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
            protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new MolWtCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new MolWtCompartorAsc());
    }
    
    public void sortByPi(ProteinComparisonDataset dataset, SORT_ORDER sortOrder) {
        List<ComparisonProtein> proteins = dataset.getProteins();
        sortByPi(sortOrder, proteins);
    }

    private void sortByPi(SORT_ORDER sortOrder, List<ComparisonProtein> proteins) {
        for(ComparisonProtein protein: proteins) {
            if(protein.molWtAndPiSet())
                continue;
            // get the protein properties
            String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
            protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
            protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteins, new PiCompartorDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteins, new PiCompartorAsc());
    }
    
//    private static class NameCompartor implements Comparator<ComparisonProtein> {
//
//        @Override
//        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
//            if(o1.getCommonName() == null && o2.getCommonName() == null)
//                return 0;
//            if(o1.getCommonName() == null || o1.getCommonName().length() == 0)
//                return 1;
//            if(o2.getCommonName() == null || o2.getCommonName().length() == 0)
//                return -1;
//            
//            return o1.getCommonName().compareTo(o2.getCommonName());
//        }
//        
//    }
    
    
    public void sortByPeptideCount(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) throws SQLException {
        
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new Comparator<ComparisonProteinGroup>() {
                @Override
                public int compare(ComparisonProteinGroup o1,
                        ComparisonProteinGroup o2) {
                    return Integer.valueOf(o2.getMaxPeptideCount()).compareTo(o1.getMaxPeptideCount());
                }});
        
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new Comparator<ComparisonProteinGroup>() {
                @Override
                public int compare(ComparisonProteinGroup o1,
                        ComparisonProteinGroup o2) {
                    return Integer.valueOf(o1.getMaxPeptideCount()).compareTo(o2.getMaxPeptideCount());
                }});
    }
    
    public void sortByMolecularWeight(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) {
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            // get the protein properties
            sortByMolecularWeight(sortOrder, proteinGroup.getProteins());
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new MolWtCompartorGroupDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new MolWtCompartorGroupAsc());
    }
    
    public void sortByPi(List<ComparisonProteinGroup> proteinGroups, SORT_ORDER sortOrder) {
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            // get the protein properties
            sortByPi(sortOrder, proteinGroup.getProteins());
        }
        if(sortOrder == SORT_ORDER.DESC)
            Collections.sort(proteinGroups, new PiCompartorGroupDesc());
        if(sortOrder == SORT_ORDER.ASC)
            Collections.sort(proteinGroups, new PiCompartorGroupAsc());
    }
    
    private static class PeptideCountCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Integer.valueOf(o2.getMaxPeptideCount()).compareTo(o1.getMaxPeptideCount());
        }
    }
    
    private static class PeptideCountCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Integer.valueOf(o1.getMaxPeptideCount()).compareTo(o2.getMaxPeptideCount());
        }
    }
    
    private static class MolWtCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o2.getMolecularWeight()).compareTo(o1.getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o1.getMolecularWeight()).compareTo(o2.getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorGroupDesc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o2.getProteins().get(0).getMolecularWeight()).compareTo(o1.getProteins().get(0).getMolecularWeight());
        }
    }
    
    private static class MolWtCompartorGroupAsc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o1.getProteins().get(0).getMolecularWeight()).compareTo(o2.getProteins().get(0).getMolecularWeight());
        }
    }
    
    private static class PiCompartorDesc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o2.getPi()).compareTo(o1.getPi());
        }
    }
    
    private static class PiCompartorAsc implements Comparator<ComparisonProtein> {
        @Override
        public int compare(ComparisonProtein o1, ComparisonProtein o2) {
            return Float.valueOf(o1.getPi()).compareTo(o2.getPi());
        }
    }
    
    private static class PiCompartorGroupDesc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o2.getProteins().get(0).getPi()).compareTo(o1.getProteins().get(0).getPi());
        }
    }
    
    private static class PiCompartorGroupAsc implements Comparator<ComparisonProteinGroup> {
        @Override
        public int compare(ComparisonProteinGroup o1, ComparisonProteinGroup o2) {
            return Float.valueOf(o1.getProteins().get(0).getPi()).compareTo(o2.getProteins().get(0).getPi());
        }
    }
}
