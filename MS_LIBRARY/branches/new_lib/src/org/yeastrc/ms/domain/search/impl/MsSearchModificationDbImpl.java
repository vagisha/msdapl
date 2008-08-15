/**
 * MsSearchModificationDbImpl.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchModificationDb;

public abstract class MsSearchModificationDbImpl implements MsSearchModificationDb {

    private int id;
    private int searchId;
    private char modifiedResidue;
    private BigDecimal modificationMass;

    public MsSearchModificationDbImpl() {
        super();
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
     * JDBC does not understand Character, so we need to return a String
     * @return
     */
    public String getModifiedResidueString() {
        return Character.toString(modifiedResidue);
    }

    /**
     * JDBC does not understand Character so we will get the value as String
     * @param modifiedResidue
     */
    public void setModifiedResidueString(String modifiedResidue) {
        if (modifiedResidue.length() > 0)
            this.modifiedResidue = modifiedResidue.charAt(0);
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

}