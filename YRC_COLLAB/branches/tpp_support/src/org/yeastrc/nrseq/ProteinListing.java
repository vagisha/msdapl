/**
 * CommonName.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.yeastrc.bio.taxonomy.TaxonomySearcher;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrProtein;



/**
 * 
 */
public class ProteinListing implements Serializable {

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
    	
    	List<ProteinReference> commonRefs = getCommonReferences();
    	if(commonRefs.size() == 0)
    		return new ArrayList<String>(0);
    	
    	Set<String> names = new HashSet<String>();
    	for(ProteinReference ref: commonRefs)
    		names.add(ref.getCommonReference().getName());
    	return new ArrayList<String>(names);
    }
    
    public List<ProteinReference> getFastaReferences() {
    	return fastaReferences;
    }
    
    public List<String> getFastaAccessions() {
    	
    	List<ProteinReference> fastaRefs = getFastaReferences();
    	if(fastaRefs.size() == 0)
    		return new ArrayList<String>(0);
    	
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
    
    private List<ProteinReference> getAllReferences() {
    	
    	List<ProteinReference> allRefs = new ArrayList<ProteinReference>();
    	allRefs.addAll(tierOneReferences);
    	allRefs.addAll(tierTwoReferences);
    	allRefs.addAll(fastaReferences);
    	allRefs.addAll(tierThreeReferences);
    	
    	return allRefs;
    }
    
    public List<ProteinReference> getDescriptionReferences() {
    	
    	List<ProteinReference> allRefs = getAllReferences();
    	allRefs = ReferenceSorter.sort(allRefs, DescriptionOrder.getOrder(this.getSpeciesId()));
    	Set<String> seen = new HashSet<String>();
    	
    	Iterator<ProteinReference> iter = allRefs.iterator();
    	while(iter.hasNext()) {
    		ProteinReference ref = iter.next();
    		if(!keepReference(ref, seen))
    			iter.remove();
    	}
    	
    	return allRefs;
    }
    
    private boolean keepReference(ProteinReference ref, Set<String> seen) {
    	if(ref.getDescription() == null || ref.getDescription().trim().length() == 0)
    		return false;
    	if(seen.contains(ref.getDescription()))
    		return false;
    	seen.add(ref.getDescription());
    	return true;
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
