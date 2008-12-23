package org.yeastrc.www.proteinfer.idpicker;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideIon;

public class WIdPickerPeptideIon {

    private int scanId;
    private IdPickerPeptideIon ion;
    private boolean uniqueToProteinGrp = false;
    
    public WIdPickerPeptideIon(IdPickerPeptideIon ion) {
        this.ion = ion;
    }

    public int getScanId() {
        return scanId;
    }

    public void setScanId(int scanId) {
        this.scanId = scanId;
    }

    public IdPickerPeptideIon getIon() {
        return ion;
    }

    public void setIon(IdPickerPeptideIon ion) {
        this.ion = ion;
    }
    
    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean isUnique) {
        this.uniqueToProteinGrp = isUnique;
    }
    
    public boolean equals(Object that) {
        if(that == this)
            return true;
        if(!(that instanceof WIdPickerPeptideIon))
            return false;
        return (this.ion.getCharge() == ((WIdPickerPeptideIon)that).getIon().getCharge() &&
                this.ion.getSequence() == ((WIdPickerPeptideIon)that).getIon().getSequence());
    }
    
    public int hashCode() {
        String identifier = this.ion.getSequence()+"_charge"+this.ion.getCharge();
        return identifier.hashCode();
    }
}
