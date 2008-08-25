/**
 * ResidueModification.java
 * @author Vagisha Sharma
 * Aug 25, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModification;

/**
 * 
 */
public class ResidueModification implements MsResidueModification {

    private char modifiedResidue;
    private char modSymbol;
    private BigDecimal modMass;
    
    public ResidueModification(char modifiedResidue, BigDecimal modMass, char modSymbol) {
        this.modifiedResidue = modifiedResidue;
        this.modMass = modMass;
        this.modSymbol = modSymbol;
    }
    public char getModifiedResidue() {return modifiedResidue;}
    public BigDecimal getModificationMass() {return modMass;}
    public char getModificationSymbol() {return modSymbol;}
    
    public void setModifiedResidue(char modifiedResidue) {
        this.modifiedResidue = modifiedResidue;
    }
    public void setModSymbol(char modSymbol) {
        this.modSymbol = modSymbol;
    }
    public void setModMass(BigDecimal modMass) {
        this.modMass = modMass;
    }
}
