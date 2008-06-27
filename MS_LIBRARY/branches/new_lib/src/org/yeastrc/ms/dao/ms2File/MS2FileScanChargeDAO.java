package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.MsScanCharge;

public interface MS2FileScanChargeDAO {

    public abstract int save(MsScanCharge scanCharge);

    public abstract MsScanCharge load(int scanChargeId);
    
    public abstract List<MsScanCharge> loadChargesForScan(int scanId);

}