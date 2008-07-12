package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.MsSearchModification;

public class StaticModification implements MsSearchModification {

    private char modificationChar;
    private BigDecimal modificationMass;
    
    public StaticModification(char modChar, BigDecimal modMass) {
        this.modificationChar = modChar;
        this.modificationMass = modMass;
    }

    /**
     * @return the modificationChar
     */
    public char getModifiedResidue() {
        return modificationChar;
    }

    /**
     * @return the modificationMass
     */
    public BigDecimal getModificationMass() {
        return modificationMass;
    }

    public char getModificationSymbol() {
        return MsSearchModification.nullCharacter;
    }

    public ModificationType getModificationType() {
        return MsSearchModification.ModificationType.STATIC;
    }

}
