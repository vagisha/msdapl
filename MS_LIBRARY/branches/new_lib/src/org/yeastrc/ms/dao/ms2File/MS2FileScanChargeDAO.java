package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.Ms2FileScanCharge;

public interface MS2FileScanChargeDAO {

    public abstract int save(Ms2FileScanCharge scanCharge);

    public abstract Ms2FileScanCharge load(int scanChargeId);
    
    public abstract List<Ms2FileScanCharge> loadChargesForScan(int scanId);

}