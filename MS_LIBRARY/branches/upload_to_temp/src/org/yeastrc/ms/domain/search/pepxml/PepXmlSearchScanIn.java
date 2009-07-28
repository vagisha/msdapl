/**
 * PepXmlSearchScanIn.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;


/**
 * 
 */
public interface PepXmlSearchScanIn extends PepXmlSearchScanBase {

    public abstract int getScanNumber();

    public abstract List<SequestPeptideProphetResultIn> getScanResults();
}

interface PepXmlSearchScanBase {
    
    public abstract BigDecimal getObservedMass();
    
    public abstract int getCharge();
    
    public abstract BigDecimal getRetentionTime();
}
