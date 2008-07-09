package org.yeastrc.ms.dto;

import java.math.BigDecimal;

public interface IMsSearchMod {

    /**
     * @return the modifiedResidue
     */
    public abstract char getModifiedResidue();

    /**
     * @return the modificationMass
     */
    public abstract BigDecimal getModificationMass();

}