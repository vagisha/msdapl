/**
 * MS2ScanCharge.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File;

import java.math.BigDecimal;
import java.util.List;


public interface MS2ScanCharge {

    /**
     * @return the charge
     */
    public abstract int getCharge();

    /**
     * @return the mass
     */
    public abstract BigDecimal getMass();

    /**
     * @return the list of charge dependent analyses for this scan + charge
     */
    public abstract List<? extends MS2Field> getChargeDependentAnalysisList();

}