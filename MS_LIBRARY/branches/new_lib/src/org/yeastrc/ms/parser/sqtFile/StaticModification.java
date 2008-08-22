package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModification;

public class StaticModification implements MsResidueModification {

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
        return '\u0000';
    }
}
