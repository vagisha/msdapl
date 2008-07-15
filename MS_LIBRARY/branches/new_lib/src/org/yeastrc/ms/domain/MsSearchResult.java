package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.util.List;

public interface MsSearchResult {

    
    public static enum ValidationStatus {VALID('V'), NOT_VALID('N'), MAYBE('M'), UNVALIDATED('U');
    
        private char statusChar;
        
        private ValidationStatus(char statusChar) { this.statusChar = statusChar; }
        
        public static ValidationStatus instance(char statusChar) {
            switch(statusChar) {
                case 'V':   return VALID;
                case 'N':   return NOT_VALID;
                case 'M':   return MAYBE;
                case 'U':   return UNVALIDATED;
                default:    return UNVALIDATED;
            }
        }
        
        public char getStatusChar() { return statusChar; }
    };
    
    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
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

    /**
     * @return the proteinMatchList
     */
    public abstract List<? extends MsSearchResultProtein> getProteinMatchList();

    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    

}