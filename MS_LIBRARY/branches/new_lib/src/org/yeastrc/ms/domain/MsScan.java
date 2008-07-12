/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.math.BigDecimal;


public interface MsScan {

    public abstract int getStartScanNum();

    public abstract int getEndScanNum();

    public abstract int getMsLevel();

    public abstract BigDecimal getPrecursorMz();
    
    public abstract int getPrecursorScanNum();
    
    public abstract BigDecimal getRetentionTime();
    
    public abstract String getFragmentationType();

    public abstract MsPeakData getPeaks();
    

}