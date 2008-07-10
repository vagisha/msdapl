package org.yeastrc.ms.domain.db;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.IMsSearchModification;
import org.yeastrc.ms.domain.IMsSearchModification.ModificationType;

public class MsPeptideSearchStaticMod implements IMsSearchModification {

    private int id;
    private int searchId;
    private char modifiedResidue;
    private BigDecimal modificationMass;
    

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
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSearchMod#getModifiedResidue()
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

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSearchMod#getModificationMass()
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

    @Override
    /**
     * Static modifications are not associated with a symbol. This method will return a null character '\u0000'
     */
    public char getModificationSymbol() {
        return nullCharacter;
    }

    public ModificationType getModificationType() {
        return ModificationType.STATIC;
    }

}