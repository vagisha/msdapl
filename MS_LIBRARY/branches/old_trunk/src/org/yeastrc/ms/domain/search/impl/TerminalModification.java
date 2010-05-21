/**
 * TerminalModification.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;

/**
 * 
 */
public class TerminalModification implements MsTerminalModificationIn {

    private BigDecimal modificationMass;
    private char modificationSymbol = EMPTY_CHAR;
    private Terminal modTerminal;
    
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
    
    public void setModificationSymbol(char modSymbol) {
        this.modificationSymbol = modSymbol;
    }
    
    @Override
    public Terminal getModifiedTerminal() {
        return modTerminal;
    }

    public void setModifiedTerminal(Terminal terminal) {
        this.modTerminal = terminal;
    }

}
