/**
 * WIdPickerProtein.java
 * @author Vagisha Sharma
 * Dec 6, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;

public class WIdPickerProtein {
    
    private IdPickerProtein idpProtein;
    private String accession = "";
    private String description = "";
    
    public WIdPickerProtein(IdPickerProtein prot) {
        this.idpProtein = prot;
    }
    public IdPickerProtein getProtein() {
        return idpProtein;
    }
    public String getAccession() {
        return accession;
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
        if(description.length() <= 40)
            return description;
        return description.substring(0, 40)+"...";
    }
    public void setDescription(String description) {
        this.description = description;
    }
}