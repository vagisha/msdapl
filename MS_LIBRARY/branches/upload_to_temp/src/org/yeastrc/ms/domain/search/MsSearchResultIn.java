package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;
import java.util.List;


public interface MsSearchResultIn extends MsRunSearchResultBase {

    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
    public abstract void setScanNumber(int scanNumber);
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProteinIn> getProteinMatchList();
    
    
    public void addMatchingProteinMatch(MsSearchResultProteinIn match) ;

}

interface MsRunSearchResultBase {
    
    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    
    public abstract void setResultPeptide(MsSearchResultPeptide resultPeptide);
    
    /**
     * @return the charge
     */
    public abstract int getCharge();
    
    public abstract void setCharge(int charge);
    
    /**
     * @return the observedMass
     */
    public abstract BigDecimal getObservedMass();
    
    public abstract void setObservedMass(BigDecimal mass);

    /**
     * @return the validationStatus
     */
    public abstract ValidationStatus getValidationStatus();
    
}