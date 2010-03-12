/**
 * CommonName.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.bio.taxonomy.TaxonomySearcher;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrProtein;



/**
 * 
 */
public class ProteinListing {

	private NrProtein protein;
	private List<ProteinReference> fastaReferences;
	private List<ProteinReference> tierOneReferences;
	private List<ProteinReference> tierTwoReferences;
	private List<ProteinReference> tierThreeReferences;
	
    
    public ProteinListing(NrProtein protein) {
    	
    	this.protein = protein;
    	tierOneReferences = new ArrayList<ProteinReference>();
    	fastaReferences = new ArrayList<ProteinReference>();
    	tierTwoReferences = new ArrayList<ProteinReference>();
    	tierThreeReferences = new ArrayList<ProteinReference>();
    }
    
    void addTierOneReference(ProteinReference reference) {
    	tierOneReferences.add(reference);
    }
    void addTierTwoReference(ProteinReference reference) {
    	tierTwoReferences.add(reference);
    }
    void addTierThreeReference(ProteinReference reference) {
    	tierThreeReferences.add(reference);
    }
    void addFastaReference(ProteinReference reference) {
    	fastaReferences.add(reference);
    }
    
    public List<ProteinReference> getCommonReferences() {
    	
    	// common references will be in the tier one references
    	List<ProteinReference> commonRefs = new ArrayList<ProteinReference>();
    	for(ProteinReference ref: tierOneReferences) {
    		if(ref.hasCommonReference()) {
    			commonRefs.add(ref);
    		}
    	}
    	
    	// If there were no tier one databases OR no common references found in tier one references
    	// look in the fasta references
    	if(commonRefs.size() == 0) {
    		for(ProteinReference ref: fastaReferences) {
        		if(ref.hasCommonReference()) {
        			commonRefs.add(ref);
        		}
        	}
    	}
    	
    	return commonRefs;
    }
    
    public List<String> getCommonNames() {
    	
    	List<ProteinReference> commonRefs = getFastaReferences();
    	Set<String> names = new HashSet<String>();
    	for(ProteinReference ref: commonRefs)
    		names.add(ref.getCommonReference().getName());
    	return new ArrayList<String>(names);
    }
    
    public List<ProteinReference> getFastaReferences() {
    	return fastaReferences;
    }
    
    public List<String> getFastaReferenceAccessions() {
    	
    	List<ProteinReference> fastaRefs = getFastaReferences();
    	Set<String> accessions = new HashSet<String>();
    	for(ProteinReference ref: fastaRefs)
    		accessions.add(ref.getAccession());
    	return new ArrayList<String>(accessions);
    }
    
    public List<ProteinReference> getBestReferences() {
    	
    	if(tierOneReferences.size() > 0)
    		return tierOneReferences;
    	else if(tierTwoReferences.size() > 0) {
    		return tierTwoReferences;
    	}
    	else if(fastaReferences.size() > 0)
    		return fastaReferences;
    	else 
    		return tierThreeReferences; // NCBI NR 
    }
    
    public List<String> getBestReferenceAccessions() {
    	
    	List<ProteinReference> bestRefs = getBestReferences();
    	Set<String> accessions = new HashSet<String>();
    	for(ProteinReference ref: bestRefs)
    		accessions.add(ref.getAccession());
    	return new ArrayList<String>(accessions);
    }
    
    public List<ProteinReference> getExternalReferences() {
    	
    	List<ProteinReference> exernalRefs = new ArrayList<ProteinReference>();
    	for(ProteinReference ref: tierOneReferences) {
    		if(ref.getHasExternalLink()) {
    			exernalRefs.add(ref);
    		}
    	}
    	
    	for(ProteinReference ref: tierTwoReferences) {
    		if(ref.getHasExternalLink()) {
    			exernalRefs.add(ref);
    		}
    	}
    	
    	for(ProteinReference ref: tierThreeReferences) {
    		if(ref.getHasExternalLink()) {
    			exernalRefs.add(ref);
    		}
    	}
    	return exernalRefs;
    }
    
    public List<ProteinReference> getAllReferences() {
    	
    	List<ProteinReference> allRefs = new ArrayList<ProteinReference>();
    	allRefs.addAll(tierOneReferences);
    	allRefs.addAll(tierTwoReferences);
    	allRefs.addAll(fastaReferences);
    	allRefs.addAll(tierThreeReferences);
    	
    	return allRefs;
    }
    
    public int getNrseqProteinId() {
        return protein.getId();
    }
    
    public String getSpeciesName() throws SQLException {
    	return TaxonomySearcher.getInstance().getName(getSpeciesId());
    }
    
    public int getSpeciesId() {
    	return protein.getSpeciesId();
    }
    
    public String getSequence() {
    	return NrSeqLookupUtil.getProteinSequence(protein.getId());
    }
}
