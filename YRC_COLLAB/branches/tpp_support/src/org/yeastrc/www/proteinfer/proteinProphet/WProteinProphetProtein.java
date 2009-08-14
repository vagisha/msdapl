/**
 * WProteinProphetProtein.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;

/**
 * 
 */
public class WProteinProphetProtein {

    private ProteinProphetProtein prophetProtein;
    private String accession = "";
    private String description = "";
    private String commonName = "";
    
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
    public WProteinProphetProtein(ProteinProphetProtein prot) {
        this.prophetProtein = prot;
    }
    public ProteinProphetProtein getProtein() {
        return prophetProtein;
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
}
