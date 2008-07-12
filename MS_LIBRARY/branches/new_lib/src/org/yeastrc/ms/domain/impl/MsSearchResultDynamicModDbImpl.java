package org.yeastrc.ms.domain.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.MsSearchResultDynamicModDb;



public class MsSearchResultDynamicModDbImpl implements MsSearchResultDynamicModDb {

    private int modId;
    private int resultId;
    private int modPosition;
    private char modifiedResidue;
    private BigDecimal modificationMass;
    private char modificationSymbol = nullCharacter;

    
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
    
    public char getModificationSymbol() {
        return modificationSymbol;
    }
    
    /**
     * @param modificationSymbol the modificationSymbol to set
     */
    public void setModificationSymbol(char modificationSymbol) {
        this.modificationSymbol = modificationSymbol;
    }
    
    /**
     * JDBC does not understand Character, so we need to return a String
     * @return
     */
    public String getModificationSymbolString() {
        return Character.toString(modificationSymbol);
    }
    /**
     * JDBC does not understand Character so we will get the value as String
     * @param modificationSymbol
     */
    public void setModificationSymbolString(String modificationSymbol) {
        if (modificationSymbol.length() > 0)
            this.modificationSymbol = modificationSymbol.charAt(0);
    }
    
    public ModificationType getModificationType() {
        return ModificationType.DYNAMIC;
    }

}
