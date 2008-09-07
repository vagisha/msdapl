/**
 * MsResidueModificationDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;

/**
 * 
 */
public class MsResidueModificationImpl implements MsResidueModification {

    private int searchId;
    private MsResidueModificationIn mod;

    public MsResidueModificationImpl(MsResidueModificationIn mod, int searchId) {
        this.mod = mod;
        this.searchId = searchId;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    @Override
    public char getModifiedResidue() {
        return mod.getModifiedResidue();
    }

    @Override
    public BigDecimal getModificationMass() {
        return mod.getModificationMass();
    }

    @Override
    public char getModificationSymbol() {
        return mod.getModificationSymbol();
    }
   
}
