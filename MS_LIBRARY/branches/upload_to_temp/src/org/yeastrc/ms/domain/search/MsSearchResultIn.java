package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;
import java.util.List;


public interface MsSearchResultIn extends MsRunSearchResultBase {

    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProteinIn> getProteinMatchList();

}

interface MsRunSearchResultBase {
    
    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    
    /**
     * @return the charge
     */
    public abstract int getCharge();
    
    /**
     * @return the observedMass
     */
    public abstract BigDecimal getObservedMass();

    /**
     * @return the validationStatus
     */
    public abstract ValidationStatus getValidationStatus();
    
}