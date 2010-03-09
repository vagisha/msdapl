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



/**
 * 
 */
public class ProteinListing {

    private int nrseqProteinId;
    private List<ProteinCommonReference> commonReferences;
    private List<ProteinNameDescription> references;
    
    
    public ProteinListing(int nrseqId) {
    	this.nrseqProteinId = nrseqId;
    	commonReferences = new ArrayList<ProteinCommonReference>();
    	references = new ArrayList<ProteinNameDescription>();
    }
    public void addReference(ProteinNameDescription reference) {
    	references.add(reference);
    }
    
    public List<ProteinNameDescription> getReferences() {
    	return this.references;
    }
    
    public void addCommonReference(ProteinCommonReference commonReference) {
    	commonReferences.add(commonReference);
    }
    
    public int getReferenceCount() {
        return this.references.size();
    }
    
    public int getCommonReferenceCount() {
    	return this.commonReferences.size();
    }
    
    public List<ProteinNameDescription> getUniqueReferencesForNonStandardDatabases() throws SQLException {
    	
    	List<ProteinNameDescription> refs = new ArrayList<ProteinNameDescription>();
    	Set<String> seen = new HashSet<String>();
    	for(ProteinNameDescription ref: this.references) {
    		if(StandardDatabase.isStandardDatabase(ref.getDatabaseName()))
    			continue;
    		if(seen.contains(ref.getAccession()))
    			continue;
    		seen.add(ref.getAccession());
    		refs.add(ref);
    	}
    	return refs;
    }
    
    public List<ProteinNameDescription> getUniqueExternalReferences() throws SQLException {
    	
    	List<ProteinNameDescription> refs = new ArrayList<ProteinNameDescription>();
    	Set<Integer> seen = new HashSet<Integer>();
    	for(ProteinNameDescription ref: this.references) {
    		if(ref.getUrl() == null) // no link to an external source
    			continue;
    		if(seen.contains(ref.getDatabaseId()))
    			continue;
    		seen.add(ref.getDatabaseId());
    		refs.add(ref);
    	}
    	return refs;
    }
    
    
    public List<ProteinNameDescription> getReferencesForUniqueDescriptions() {
    	
    	Set<String> seen = new HashSet<String>();
    	List<ProteinNameDescription> unique = new ArrayList<ProteinNameDescription>();
    	for(ProteinNameDescription ref: references) {
    		if(seen.contains(ref.getDescription()))
    			continue;
    		seen.add(ref.getDescription());
    		unique.add(ref);
    	}
    	return unique;
    }
    
    List<ProteinNameDescription> getReferencesForDatabase(String dbName) throws SQLException {
    	
    	List<ProteinNameDescription> refs = new ArrayList<ProteinNameDescription>();
    	for(ProteinNameDescription ref: references) {
    		if(ref.getDatabaseName().equals(dbName))
    			refs.add(ref);
    	}
    	return refs;
    }
    
    List<String> getAccessionsForDatabase(String databaseName) throws SQLException {
    	
    	List<ProteinNameDescription> refs = getReferencesForDatabase(databaseName);
    	Set<String> accessions = new HashSet<String>();
    	for(ProteinNameDescription ref: refs) {
    		accessions.add(ref.getAccession());
    	}
    	return new ArrayList<String>(accessions);
    }
    
    List<String> getAccessionsForNotStandardDatabases() throws SQLException {
    	
    	List<ProteinNameDescription> refs = getUniqueReferencesForNonStandardDatabases();
    	Set<String> accessions = new HashSet<String>();
    	for(ProteinNameDescription ref: refs) {
    		accessions.add(ref.getAccession());
    	}
    	return new ArrayList<String>(accessions);
    }
    
    public List<ProteinCommonReference> getCommonReferences() {
    	return this.commonReferences;
    }
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    
}
