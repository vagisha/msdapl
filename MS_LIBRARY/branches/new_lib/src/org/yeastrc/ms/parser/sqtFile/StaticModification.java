package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;

public class StaticModification {

    private char modificationChar;
    private BigDecimal modificationMass;
    
    public StaticModification(char modChar, BigDecimal modMass) {
        this.modificationChar = modChar;
        this.modificationMass = modMass;
    }

    /**
     * @return the modificationChar
     */
    public char getModificationChar() {
        return modificationChar;
    }

    /**
     * @return the modificationMass
     */
    public BigDecimal getModificationMass() {
        return modificationMass;
    }

}
