/**
 * MsResultDynamicTerminalModDbImpl.java
 * @author Vagisha Sharma
 * Aug 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResultDynamicTerminalModDb;

/**
 * 
 */
public class MsResultDynamicTerminalModDbImpl implements
        MsResultDynamicTerminalModDb {

    private int modId;
    private int resultId;
    private Terminal modifiedTerminal;
    private BigDecimal modificationMass;
    private char modificationSymbol;
    
    @Override
    public int getModificationId() {
        return modId;
    }

    public void setModificationId(int modId) {
        this.modId = modId;
    }
    
    @Override
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    @Override
    public Terminal getModifiedTerminal() {
        return modifiedTerminal;
    }

    public void setModifiedTerminal(Terminal terminal) {
        this.modifiedTerminal = terminal;
    }
    
    @Override
    public BigDecimal getModificationMass() {
        return modificationMass;
    }

    public void setModificationMass(BigDecimal modificationMass) {
        this.modificationMass = modificationMass;
    }
    
    @Override
    public char getModificationSymbol() {
        return modificationSymbol;
    }

    public void setModificationSymbol(char modificationSymbol) {
        this.modificationSymbol = modificationSymbol;
    }
}
