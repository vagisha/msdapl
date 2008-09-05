package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;



public class MsResultResidueModImpl implements MsResultDynamicResidueMod {

    private int modId;
    private int resultId;
    private int modPosition;
    private char modifiedResidue = EMPTY_CHAR;
    private BigDecimal modificationMass;
    private char modificationSymbol = EMPTY_CHAR;
    
    /**
     * @return the modId
     */
    public int getModificationId() {
        return modId;
    }
    /**
     * @param modId the modId to set
     */
    public void setModificationId(int modId) {
        this.modId = modId;
    }
    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    /**
     * @return the modPosition
     */
    public int getModifiedPosition() {
        return modPosition;
    }
    /**
     * @param modPosition the modPosition to set
     */
    public void setModifiedPosition(int modPosition) {
        this.modPosition = modPosition;
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
    
    /**
     * @param modificationSymbol the modificationSymbol to set
     */
    public void setModificationSymbol(char modificationSymbol) {
        this.modificationSymbol = modificationSymbol;
    }

}
