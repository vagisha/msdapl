/**
 * MsResidueModificationDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModification;

/**
 * 
 */
public class MsResidueModificationImpl implements MsResidueModification {

    private int id;
    private int searchId;
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
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    /**
     * @param searchId the serachId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
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
