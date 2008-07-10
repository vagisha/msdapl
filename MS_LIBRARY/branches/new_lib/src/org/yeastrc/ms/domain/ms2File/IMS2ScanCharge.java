package org.yeastrc.ms.domain.ms2File;

import java.math.BigDecimal;
import java.util.List;


public interface IMS2ScanCharge {

    /**
     * @return the charge
     */
    public abstract int getCharge();

    /**
     * @return the mass
     */
    public abstract BigDecimal getMass();

    /**
     * @return the chargeDepAnalysis
     */
    public abstract List<? extends IHeader> getChargeDependentAnalysisList();

}