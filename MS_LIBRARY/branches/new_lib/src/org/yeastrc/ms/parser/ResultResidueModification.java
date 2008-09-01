/**
 * ResultResidueMod.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;

/**
 * 
 */
public class ResultResidueModification implements MsResultDynamicResidueMod {

    private char modResidue;
    private char modSymbol = '\u0000';
    private BigDecimal modMass;
    private int position;

    public ResultResidueModification(char modResidue, char modSymbol, BigDecimal modMass, int position) {
        this.modResidue = modResidue;
        this.modSymbol = modSymbol;
        this.modMass = modMass;
        this.position = position;
    }
    
    public ResultResidueModification(char modResidue, BigDecimal modMass, int position) {
        this.modResidue = modResidue;
        this.modMass = modMass;
        this.position = position;
    }
    
    public BigDecimal getModificationMass() {
        return modMass;
    }

    public char getModificationSymbol() {
        return modSymbol;
    }

    public char getModifiedResidue() {
        return modResidue;
    }
    
    public int getModifiedPosition() {
        return this.position;
    }
}
