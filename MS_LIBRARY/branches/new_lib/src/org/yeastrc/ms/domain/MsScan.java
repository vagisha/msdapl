/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.util.Iterator;



public interface MsScan extends MsScanBase {

    /**
     * String[0] = m/z; String[1] = RT
     * @return
     */
    public Iterator<String[]> peakIterator();

}

interface MsScanBase {

    public abstract int getStartScanNum();

    public abstract int getEndScanNum();

    public abstract int getMsLevel();

    public abstract BigDecimal getPrecursorMz();

    public abstract int getPrecursorScanNum();

    public abstract BigDecimal getRetentionTime();

    public abstract String getFragmentationType();

}