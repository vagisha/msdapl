/**
 * SubsetProteinFinder.java
 * @author Vagisha Sharma
 * Oct 18, 2010
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;

/**
 * 
 */
public class SubsetProteinFinder {

	private static final Logger log = Logger.getLogger(SubsetProteinFinder.class);
	
	public void markSubsetProteins(List<? extends InferredProtein<? extends SpectrumMatch>> inputProteinList) {
		
		
		// Make a copy of the list; we will be sorting this list
		List<InferredProtein<? extends SpectrumMatch>> proteinList = new ArrayList<InferredProtein<? extends SpectrumMatch>>(inputProteinList.size());
		for(InferredProtein<? extends SpectrumMatch> protein: inputProteinList)
			proteinList.add(protein);
				
		Set<String> peptides = new HashSet<String>();
		
		// sort the list by cluster ID; We will look at one cluster at a time;
		Collections.sort(proteinList, new Comparator<InferredProtein<? extends SpectrumMatch>>() {
			@Override
			public int compare(InferredProtein<? extends SpectrumMatch> o1, InferredProtein<? extends SpectrumMatch> o2) {
				return Integer.valueOf(o1.getProteinClusterId()).compareTo(o2.getProteinClusterId());
			}
		});
		
		
		int clusterId = -1;
		
		List<InferredProtein<? extends SpectrumMatch>> clusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
		Set<String> clusterPeptides = new HashSet<String>();
		
		Set<Integer> subsetProteinGroupIds = new HashSet<Integer>();
		for(InferredProtein<? extends SpectrumMatch> iProtein: proteinList) {
			if(iProtein.getProteinClusterId() != clusterId) {
				
				if(clusterId != -1) {
					
					// make sure that the peptides in this cluster were unique to this cluster
					for(String peptide: clusterPeptides) {
						if(peptides.contains(peptide)) {
							log.error("Petide "+peptide+" in cluster "+clusterId+" also found in another cluster");
						}
						peptides.add(peptide);
					}
					Set<Integer> subsetGrpIds = getSubsetProteinGroupIds(clusterProteins);
					subsetProteinGroupIds.addAll(subsetGrpIds);
				}
				clusterProteins.clear();
				clusterPeptides.clear();
				clusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
				clusterPeptides = new HashSet<String>();
				clusterId = iProtein.getProteinClusterId();
			}
			clusterProteins.add(iProtein);

			for(PeptideEvidence<? extends SpectrumMatch> pev: iProtein.getPeptides()) {
				clusterPeptides.add(pev.getPeptide().getPeptideSequence());
			}
		}
		
		// last one
		// make sure that the peptides in this cluster were unique to this cluster
		for(String peptide: clusterPeptides) {
			if(peptides.contains(peptide)) {
				log.error("Petide "+peptide+" in cluster "+clusterId+" also found in another cluster");
			}
			peptides.add(peptide);
		}
		Set<Integer> subsetGrpIds = getSubsetProteinGroupIds(clusterProteins);
		subsetProteinGroupIds.addAll(subsetGrpIds);
		
		// mark the subset proteins
		for(InferredProtein<? extends SpectrumMatch> protein: inputProteinList) {
			if(subsetProteinGroupIds.contains(protein.getProteinGroupId()))
				protein.getProtein().setSubset(true);
		}
	}

	private Set<Integer> getSubsetProteinGroupIds(List<InferredProtein<? extends SpectrumMatch>> clusterProteins) {
		
		
		// get one representative from each indistinguishable protein groups
		List<InferredProtein<? extends SpectrumMatch>> sparseClusterProteins = new ArrayList<InferredProtein<? extends SpectrumMatch>>();
		Set<Integer> groupIds = new HashSet<Integer>();
		Map<String, Set<Integer>> peptideProteinMap = new HashMap<String, Set<Integer>>();
		for(InferredProtein<? extends SpectrumMatch> iProtein: clusterProteins) {
			
			if(groupIds.contains(iProtein.getProteinGroupId()))
				continue;
			
			groupIds.add(iProtein.getProteinGroupId());
			sparseClusterProteins.add(iProtein);
		}
		
		// Create a map of peptides and matching proteins.
		for(InferredProtein<? extends SpectrumMatch> iProtein: sparseClusterProteins) {
			
			for(PeptideEvidence<? extends SpectrumMatch> pev: iProtein.getPeptides()) {
				
				Set<Integer> matchingProteins = peptideProteinMap.get(pev.getPeptide().getPeptideSequence());
				if(matchingProteins == null) {
					matchingProteins = new HashSet<Integer>();
					peptideProteinMap.put(pev.getPeptide().getPeptideSequence(), matchingProteins);
				}
				matchingProteins.add(iProtein.getProtein().getId());
			}
		}
		
		
		// Look for proteins whose peptides are a subset of peptides of another protein
		Set<Integer> subsetGroupIds = new HashSet<Integer>();
		for(int i = 0; i < sparseClusterProteins.size(); i++) {
			
			for(int j = 0; j < sparseClusterProteins.size(); j++) {
				
				if(i == j)
					continue;
				
				InferredProtein<? extends SpectrumMatch> protein_i = sparseClusterProteins.get(i);
				InferredProtein<? extends SpectrumMatch> protein_j = sparseClusterProteins.get(j);
				
				if(isSubset(protein_i, protein_j)) {
					subsetGroupIds.add(protein_i.getProteinGroupId());
					break;
				}
			}
		}
		return subsetGroupIds;
	}

	private boolean isSubset(InferredProtein<? extends SpectrumMatch> protein_i,
			InferredProtein<? extends SpectrumMatch> protein_j) {
		
		Set<String> peptides_i = new HashSet<String>();
		for(PeptideEvidence<? extends SpectrumMatch> pev: protein_i.getPeptides()) {
			peptides_i.add(pev.getPeptide().getPeptideSequence());
		}
		
		Set<String> peptides_j = new HashSet<String>();
		for(PeptideEvidence<? extends SpectrumMatch> pev: protein_j.getPeptides()) {
			peptides_j.add(pev.getPeptide().getPeptideSequence());
		}
		
		if(peptides_j.containsAll(peptides_i))
			return true;
		else
			return false;
	}
}
