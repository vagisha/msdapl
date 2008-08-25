/**
 * TerminalModification.java
 * @author Vagisha Sharma
 * Aug 25, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsTerminalModification;

/**
 * 
 */
public class TerminalModification implements MsTerminalModification {

    private BigDecimal modMass;
    private Terminal terminal;
    private char modSymbol = '\u0000';

    public TerminalModification(Terminal modTerminal, BigDecimal modMass, char modSymbol) {
        this.modMass = modMass;
        this.terminal = modTerminal;
        this.modSymbol = modSymbol;
    }
    
    public BigDecimal getModificationMass() {return modMass;}
    public char getModificationSymbol() {return modSymbol;}
    public Terminal getModifiedTerminal() {return terminal;}
    
    
    public void setModificationMass(BigDecimal modMass) {
        this.modMass = modMass;
    }
    public void setModifiedTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
    public void setModificationSymbol(char modSymbol) {
        this.modSymbol = modSymbol;
    }
}
