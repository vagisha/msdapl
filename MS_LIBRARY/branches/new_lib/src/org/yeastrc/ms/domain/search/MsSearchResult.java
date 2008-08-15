package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;
import java.util.List;


public interface MsSearchResult extends MsSearchResultBase {

    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProtein> getProteinMatchList();

    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    
}

interface MsSearchResultBase {
    
    /**
     * @return the charge
     */
    public abstract int getCharge();

    /**
     * @return the calculatedMass
     */
    public abstract BigDecimal getCalculatedMass();

    /**
     * @return the numIonsMatched
     */
    public abstract int getNumIonsMatched();

    /**
     * @return the numPredictedIons
     */
    public abstract int getNumIonsPredicted();

    /**
     * @return the validationStatus
     */
    public abstract ValidationStatus getValidationStatus();
    
}