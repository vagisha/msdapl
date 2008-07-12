/**
 * MsSearchModification.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.math.BigDecimal;

public interface MsSearchModification {

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
    
    
    /**
     * @return the modification type (STATIC or DYNAMIC)
     */
    public abstract ModificationType getModificationType();

}