package org.yeastrc.ms.dto;

import java.math.BigDecimal;


public class MsSearchResultDynamicMod {

    private char modifiedResidue;
    private BigDecimal modificationMass;
    private int modPosition;
    private char modSymbol;
    
    /**
     * @return the modPosition
     */
    public int getModificationPosition() {
        return modPosition;
    }
    /**
     * @param modPosition the modPosition to set
     */
    public void setModificationPosition(int modPosition) {
        this.modPosition = modPosition;
    }
   
    /**
     * @return the modSymbol
     */
    public char getModificationSymbol() {
        return modSymbol;
    }
    /**
     * @param modSymbol the modSymbol to set
     */
    public void setModificationSymbol(char modSymbol) {
        this.modSymbol = modSymbol;
    }
    
    /**
     * @return the modifiedResidue
     */
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
        return new Character(modifiedResidue).toString();
    }

    /**
     * JDBC does not understand Character so we will get the value as String
     * @param modifiedResidue
     */
    public void setModifiedResidueString(String modifiedResidue) {
        if (modifiedResidue.length() > 0)
            this.modifiedResidue = modifiedResidue.charAt(0);
    }

    /**
     * @return the modificationMass
     */
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
