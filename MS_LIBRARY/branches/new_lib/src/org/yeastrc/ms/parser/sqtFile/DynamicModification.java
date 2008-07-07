package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;

public class DynamicModification {

    private char modificationChar;
    private BigDecimal modificationMass;
    private char modificationSymbol;
    
    public DynamicModification(char modChar, BigDecimal modMass, char modSymbol) {
        this.modificationChar = modChar;
        this.modificationMass = modMass;
        this.modificationSymbol = modSymbol;
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

    /**
     * @return the modificationSymbol
     */
    public char getModificationSymbol() {
        return modificationSymbol;
    }
}
