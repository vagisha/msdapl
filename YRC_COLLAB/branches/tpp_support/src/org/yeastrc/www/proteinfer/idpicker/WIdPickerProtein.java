/**
 * WIdPickerProtein.java
 * @author Vagisha Sharma
 * Dec 6, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.nrseq.ProteinCommonReference;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.nrseq.ProteinReference;

public class WIdPickerProtein {
    
    private IdPickerProteinBase idpProtein;
    private ProteinListing listing;
    private float molecularWeight = -1.0f;
    private float pi = -1.0f;
    
    public WIdPickerProtein(IdPickerProteinBase prot) {
        this.idpProtein = prot;
    }
    
    public void setProteinListing(ProteinListing listing) {
    	this.listing = listing;
    }
    
    public ProteinListing getProteinListing() {
    	return this.listing;
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
    
    public ProteinReference getBestDescriptionReference() throws SQLException {
    	return listing.getBestReference();
    }
    
    public List<ProteinReference> getUniqueDbDescriptionReferences() throws SQLException {
    	return listing.getReferencesForUniqueDatabases();
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
    
    public IdPickerProteinBase getProtein() {
        return idpProtein;
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