package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

public interface MsModification {

    public enum ModificationType {STATIC, DYNAMIC};
    
    public abstract BigDecimal getModificationMass();

    public abstract char getModificationSymbol();
    
    public ModificationType getModificationType();

}