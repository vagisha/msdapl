package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

public interface MsModification {

    public static final char EMPTY_CHAR = '\u0000';
    
    public abstract BigDecimal getModificationMass();

    public abstract char getModificationSymbol();
}