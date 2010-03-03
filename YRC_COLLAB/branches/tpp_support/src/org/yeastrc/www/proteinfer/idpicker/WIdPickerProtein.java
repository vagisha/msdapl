/**
 * WIdPickerProtein.java
 * @author Vagisha Sharma
 * Dec 6, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;

public class WIdPickerProtein {
    
    private IdPickerProteinBase idpProtein;
    private String accession = "";
    private String description = "";
    private String commonName = "";
    private float molecularWeight = -1.0f;
    private float pi = -1.0f;
    
    public String getCommonName() {
        return commonName;
    }
    public String getCommonNameShort() {
        if(commonName == null)
            return "";
        if(commonName.length() <= 15)
            return commonName;
        return commonName.substring(0, 15)+"...";
    }
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    public WIdPickerProtein(IdPickerProteinBase prot) {
        this.idpProtein = prot;
    }
    public IdPickerProteinBase getProtein() {
        return idpProtein;
    }
    public String getAccession() {
        return accession;
    }
    public String getShortAccession() {
        if(accession == null)
            return "No Accession";
        if(accession.length() <= 15)
            return accession;
        return accession.substring(0, 15)+"...";
    }
    public void setAccession(String accession) {
        this.accession = accession;
    }
    public String getDescription() {
        return description;
    }
    public String getShortDescription() {
        if(description == null)
            return "No Description";
        if(description.length() <= 90)
            return description;
        return description.substring(0, 90)+"...";
    }
    public void setDescription(String description) {
        this.description = description;
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