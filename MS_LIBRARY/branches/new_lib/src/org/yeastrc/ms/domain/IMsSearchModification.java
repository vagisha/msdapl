package org.yeastrc.ms.domain;

import java.math.BigDecimal;

public interface IMsSearchModification {

    public static final char nullCharacter = '\u0000';
    
    public static enum ModificationType {STATIC, DYNAMIC;};

    /**
     * @return the modifiedResidue
     */
    public abstract char getModifiedResidue();

    /**
     * @return the modificationMass
     */
    public abstract BigDecimal getModificationMass();
    
    /**
     * @return the modificationSymbol
     */
    public abstract char getModificationSymbol();
    
    
    public abstract ModificationType getModificationType();

}