package org.yeastrc.ms.domain;

import java.math.BigDecimal;

public interface MsScanBase {

    public abstract int getStartScanNum();

    public abstract int getEndScanNum();

    public abstract int getMsLevel();

    public abstract BigDecimal getPrecursorMz();

    public abstract int getPrecursorScanNum();

    public abstract BigDecimal getRetentionTime();

    public abstract String getFragmentationType();

}