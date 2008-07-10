package org.yeastrc.ms.domain;

import java.math.BigDecimal;


public interface IMsScan {

    public abstract int getStartScanNum();

    public abstract int getEndScanNum();

    public abstract int getMsLevel();

    public abstract String getFragmentationType();

    public abstract BigDecimal getPrecursorMz();

    public abstract IPeaks getPeaks();

    public abstract int getPrecursorScanNum();

}