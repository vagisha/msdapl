/**
 * MsResultDynamicResidueMod.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

/**
 * 
 */
public interface MsResultDynamicResidueMod {

    public char getModifiedResidue();
    
    public abstract BigDecimal getModificationMass();

    public abstract char getModificationSymbol();
}
