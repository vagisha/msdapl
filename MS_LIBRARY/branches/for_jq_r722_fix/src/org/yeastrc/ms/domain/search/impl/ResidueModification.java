/**
 * ResidueModification.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;

/**
 * 
 */
public class ResidueModification implements MsResidueModificationIn {

    private BigDecimal modificationMass;
    private char modificationSymbol = EMPTY_CHAR;
    private char modifiedResidue = EMPTY_CHAR;
    
    public char getModifiedResidue() {
        return modifiedResidue;
    }

    /**
     * @param modifiedResidue the modifiedResidue to set
     */
    public void setModifiedResidue(char modifiedResidue) {
        this.modifiedResidue = modifiedResidue;
    }
    
    public BigDecimal getModificationMass() {
        return modificationMass;
    }

    /**
     * @param modificationMass the modificationMass to set
     */
    public void setModificationMass(BigDecimal modificationMass) {
        this.modificationMass = modificationMass;
    }
    
    public char getModificationSymbol() {
        return modificationSymbol;
    }
    
    public void setModificationSymbol(char modSymbol) {
        this.modificationSymbol = modSymbol;
    }

}
