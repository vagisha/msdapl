package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.MS2FileScanCharge;

public interface MS2FileScanChargeDAO {

    public abstract int save(MS2FileScanCharge scanCharge);

    public abstract MS2FileScanCharge load(int scanChargeId);
    
    public abstract List<MS2FileScanCharge> loadChargesForScan(int scanId);

}