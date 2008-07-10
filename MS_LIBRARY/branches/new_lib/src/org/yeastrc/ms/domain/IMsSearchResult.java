package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.db.MsSearchResultPeptide;

public interface IMsSearchResult {

    
    public static enum ValidationStatus {VALID('V'), NOT_VALID('N'), MAYBE('M'), UNVALIDATED('U');
    
        private char statusChar;
        
        private ValidationStatus(char statusChar) { this.statusChar = statusChar; }
        
        public static ValidationStatus getStatusForChar(char statusChar) {
            switch(statusChar) {
                case 'V':   return VALID;
                case 'N':   return NOT_VALID;
                case 'M':   return MAYBE;
                case 'U':   return UNVALIDATED;
                default:    return null;
            }
        }
        
        public char getStatusChar() { return statusChar; }
    };
    
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
    public abstract List<? extends IMsSearchResultProtein> getProteinMatchList();

    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    

}