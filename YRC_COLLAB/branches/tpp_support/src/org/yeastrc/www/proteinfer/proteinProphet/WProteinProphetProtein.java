/**
 * WProteinProphetProtein.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.nrseq.ProteinCommonReference;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.nrseq.ProteinReference;

/**
 * 
 */
public class WProteinProphetProtein {

    private ProteinProphetProtein prophetProtein;
    private ProteinListing listing;
    private float molecularWeight = -1.0f;
    private float pi = -1.0f;
    
    public WProteinProphetProtein(ProteinProphetProtein prot) {
        this.prophetProtein = prot;
    }
    public ProteinProphetProtein getProtein() {
        return prophetProtein;
    }
    
    public void setProteinListing(ProteinListing listing) {
    	this.listing = listing;
    }
    
    public List<ProteinReference> getFastaReferences() throws SQLException {
    	return listing.getUniqueReferencesForNonStandardDatabases();
    }
    
    public String getAccessionsCommaSeparated() throws SQLException {
    	List<String> accessions = new ArrayList<String>();
    	List<ProteinReference> refs = getFastaReferences();
    	for(ProteinReference ref: refs)
    		accessions.add(ref.getAccession());
    	return StringUtils.makeCommaSeparated(accessions);
    }
    
    public List<ProteinReference> getExternalReferences() throws SQLException {
    	return listing.getUniqueExternalReferences();
    }
    
    public List<ProteinReference> getDescriptionReferences() throws SQLException {
    	return listing.getReferencesForUniqueDescriptions();
    }
    
    public List<ProteinReference> getUniqueDbDescriptionReferences() throws SQLException {
    	return listing.getReferencesForUniqueDatabases();
    }
    
    public ProteinReference getOneDescriptionReference() throws SQLException {
    	return listing.getReferences().get(0);
    }
    
    public List<ProteinCommonReference> getCommonReferences() {
    	return listing.getCommonReferences();
    }
    
    public String getCommonNamesCommaSeparated() throws SQLException {
    	List<String> names = new ArrayList<String>();
    	List<ProteinCommonReference> refs = getCommonReferences();
    	for(ProteinCommonReference ref: refs)
    		names.add(ref.getName());
    	return StringUtils.makeCommaSeparated(names);
    }
    
    public void setMolecularWeight(float weight) {
        this.molecularWeight = weight;
    }
    
    public float getMolecularWeight() {
        return this.molecularWeight;
    }
    
    public float getPi() {
        return pi;
    }
    
    public void setPi(float pi) {
        this.pi = pi;
    }
}
