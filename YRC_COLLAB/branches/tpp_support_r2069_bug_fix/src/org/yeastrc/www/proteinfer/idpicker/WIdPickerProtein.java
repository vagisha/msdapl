/**
 * WIdPickerProtein.java
 * @author Vagisha Sharma
 * Dec 6, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.util.StringUtils;
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
    
    public String getAccessionsCommaSeparated() throws SQLException {
    	List<String> accessions = listing.getFastaAccessions();
    	return StringUtils.makeCommaSeparated(accessions);
    }
    
    public ProteinReference getOneDescriptionReference() throws SQLException {
    	if(listing.getDescriptionReferences().size() > 0)
    		return listing.getDescriptionReferences().get(0);
    	return null;
    }
    
    public String getCommonNamesCommaSeparated() throws SQLException {
    	List<String> names = listing.getCommonNames();
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