package org.yeastrc.ms.domain.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.IMsScan;

public interface IMS2Scan extends IMsScan {

    /**
     * @return the scanChargeList
     */
    public abstract List<? extends IMS2ScanCharge> getScanChargeList();

    /**
     * @return the chargeIndependentAnalysisList
     */
    public abstract List<? extends IHeader> getChargeIndependentAnalysisList();

}