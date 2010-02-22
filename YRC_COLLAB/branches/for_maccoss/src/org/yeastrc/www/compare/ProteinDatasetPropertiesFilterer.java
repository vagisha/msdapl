/**
 * ProteinDatasetPropertiesFilterer.java
 * @author Vagisha Sharma
 * Feb 18, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

import edu.uwpr.protinfer.util.ProteinUtils;

/**
 * 
 */
public class ProteinDatasetPropertiesFilterer {

    private static ProteinDatasetPropertiesFilterer instance;

    public static ProteinDatasetPropertiesFilterer instance() {
        if(instance == null) 
            instance = new ProteinDatasetPropertiesFilterer();
        return instance;
    }

    public void applyFastaNameFilter(List<ComparisonProtein> proteins, String searchString) throws SQLException {

        List<Integer> sortedIds = getSortedNrseqIdsMatchingName(searchString);
        if(sortedIds == null || sortedIds.size() == 0)
            return;

        // Remove the ones that do not match
        removeNonMatching(proteins, sortedIds);
    }

    public void applyDescriptionFilter(List<ComparisonProtein> proteins, List<Integer> fastaDatabaseIds, String searchString) throws SQLException {

        List<Integer> sortedIds = getSortedNrseqIdsMatchingDescription(fastaDatabaseIds,searchString);
        if(sortedIds == null || sortedIds.size() == 0)
            return;

        removeNonMatching(proteins, sortedIds);
    }
    
    private void removeNonMatching(List<ComparisonProtein> proteins,
            List<Integer> sortedIds) {
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein prot = iter.next();
            if(Collections.binarySearch(sortedIds, prot.getNrseqId()) < 0)
                iter.remove();
        }
    }
    
    public void applyFastaNameFilterToGroup(List<ComparisonProteinGroup> proteins, String searchString) throws SQLException {

        List<Integer> sortedIds = getSortedNrseqIdsMatchingName(searchString);
        if(sortedIds == null || sortedIds.size() == 0)
            return;

        // Remove the ones that do not match
        removeNonMatchingGroup(proteins, sortedIds);
    }

    public void applyDescriptionFilterToGroup(List<ComparisonProteinGroup> proteins, List<Integer> fastaDatabaseIds, String searchString) throws SQLException {

        List<Integer> sortedIds = getSortedNrseqIdsMatchingDescription(fastaDatabaseIds,searchString);
        if(sortedIds == null || sortedIds.size() == 0)
            return;

        removeNonMatchingGroup(proteins, sortedIds);
    }
    
    private void removeNonMatchingGroup(List<ComparisonProteinGroup> proteins,
            List<Integer> sortedIds) {
        Iterator<ComparisonProteinGroup> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProteinGroup protGrp = iter.next();
            // remove a protein group from the list only if none of the proteins in this group don't match the search string
            boolean matches = false;
            for(ComparisonProtein prot: protGrp.getProteins()) {
                if(Collections.binarySearch(sortedIds, prot.getNrseqId()) >= 0) {
                    matches = true;
                    break;
                }
            }
            if(!matches)
                iter.remove();
        }
    }
    
    public void applyPiFilter(List<ComparisonProtein> proteins, double minPi, double maxPi) {
        initializeProteinProperties(proteins);
        Iterator<ComparisonProtein> protIter = proteins.iterator();
        while(protIter.hasNext()) {
            ComparisonProtein prot = protIter.next();
            float pi = prot.getPi();
            if(pi < minPi || pi > maxPi)
                protIter.remove();
        }
    }

    public void applyMolecularWtFilter(List<ComparisonProtein> proteins, double minMolWt, double maxMolWt) {
        initializeProteinProperties(proteins);
        Iterator<ComparisonProtein> protIter = proteins.iterator();
        while(protIter.hasNext()) {
            ComparisonProtein prot = protIter.next();
            float molWt = prot.getMolecularWeight();
            if(molWt < minMolWt || molWt > maxMolWt)
                protIter.remove();
        }
    }
    
    public void applyPiFilterToGroup(List<ComparisonProteinGroup> proteinGroups, double minPi, double maxPi) {
        
        for(ComparisonProteinGroup group: proteinGroups)
            initializeProteinProperties(group.getProteins());
        
        Iterator<ComparisonProteinGroup> protGrpIter = proteinGroups.iterator();
        while(protGrpIter.hasNext()) {
            ComparisonProteinGroup protGrp = protGrpIter.next();
            // remove a protein group from the list only if none of the proteins in this group don't match the filter criteria
            boolean matches = false;
            for(ComparisonProtein prot: protGrp.getProteins()) {
                float pi = prot.getPi();
                if(pi >= minPi && pi <= maxPi) {
                    matches = true;
                    break;
                }
            }
            if(!matches)
                protGrpIter.remove();
        }
    }

    public void applyMolecularWtFilterToGroup(List<ComparisonProteinGroup> proteinGroups, double minMolWt, double maxMolWt) {
        
        for(ComparisonProteinGroup group: proteinGroups)
            initializeProteinProperties(group.getProteins());
        
        Iterator<ComparisonProteinGroup> protGrpIter = proteinGroups.iterator();
        while(protGrpIter.hasNext()) {
            ComparisonProteinGroup protGrp = protGrpIter.next();
            // remove a protein group from the list only if none of the proteins in this group don't match the filter criteria
            boolean matches = false;
            for(ComparisonProtein prot: protGrp.getProteins()) {
                float molWt = prot.getMolecularWeight();
                if(molWt >= minMolWt && molWt <= maxMolWt) {
                    matches = true;
                    break;
                }
            }
            if(!matches)
                protGrpIter.remove();
        }
    }
    
    private void initializeProteinProperties(List<ComparisonProtein> proteins) {
        for(ComparisonProtein protein: proteins) {
            if(protein.molWtAndPiSet())
                continue;
            // get the protein properties
            String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
            protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
            protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
        }
    }

    
    private List<Integer> getSortedNrseqIdsMatchingName(String searchString)
                throws SQLException {

        if(searchString == null || searchString.trim().length() == 0)
            return null;

//      get the protein ids for the names the user is searching for
        Set<Integer> proteinIds = new HashSet<Integer>();
        String tokens[] = searchString.split(",");

        List<String> notFound = new ArrayList<String>();    

//      Do a common name lookup first
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

//      Now look at the accession strings in tblProteinDatabase;
        if(notFound.size() > 0) {
            for(String name: notFound) {
                List<Integer> ids = NrSeqLookupUtil.getProteinIdsForAccession(name);
                if(ids != null)
                    proteinIds.addAll(ids);
            }
        }

//      sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        return sortedIds;
    }
    
    private List<Integer> getSortedNrseqIdsMatchingDescription(List<Integer> fastaDatabaseIds, String searchString) {
        if(searchString == null || searchString.trim().length() == 0)
            return null;

        // get the protein ids for the descriptions the user is searching for
        Set<Integer> proteinIds = new HashSet<Integer>();
        String tokens[] = searchString.split(",");

        List<String> notFound = new ArrayList<String>();

        for(String token: tokens) {
            String description = token.trim();
            if(description.length() > 0) {
                List<NrDbProtein> proteins = NrSeqLookupUtil.getDbProteinsForDescription(fastaDatabaseIds, description);
                for(NrDbProtein protein: proteins)
                    proteinIds.add(protein.getProteinId());
            }
        }

        // sort the matching protein ids.
        List<Integer> sortedIds = new ArrayList<Integer>(proteinIds.size());
        sortedIds.addAll(proteinIds);
        Collections.sort(sortedIds);
        return sortedIds;
    }
}
