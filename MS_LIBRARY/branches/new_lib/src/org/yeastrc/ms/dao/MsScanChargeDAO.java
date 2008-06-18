package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.MsScanCharge;

public interface MsScanChargeDAO {

    public abstract int save(MsScanCharge scanCharge);

    public abstract MsScanCharge load(int scanChargeId);
    
    public abstract List<MsScanCharge> loadChargesForScan(int scanId);

    public abstract void update(MsScanCharge run);

}