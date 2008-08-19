package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

public interface MsModification {

    public abstract BigDecimal getModificationMass();

    public abstract char getModificationSymbol();
}