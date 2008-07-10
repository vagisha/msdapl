package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.db.MS2FileScanCharge;

public interface MS2FileScanChargeDAO {

    
    public abstract List<Integer> loadScanChargeIdsForScan(int scanId);
    
    public abstract List<MS2FileScanCharge> loadScanChargesForScan(int scanId);
    
    public abstract List<MS2FileScanCharge> loadScanChargesForScan(int scanId, int charge);
    
    
    /**
     * Saves the given MS2FileScanCharge along with associated charge dependent analyses
     * @param scanCharge
     * @return database id of the saved MS2FileScanCharge
     */
    public abstract int save(MS2FileScanCharge scanCharge);

    
    /**
     * Deletes all entries associated with the given scanId. Related charge dependent
     * analyses are deleted as well. 
     * @param scanId
     */
    public abstract void deleteByScanId(int scanId);
    
    /**
     * Deletes all entries associated with the given scanId. Related charge dependent
     * analyses are deleted as well. 
     * This method uses a single multi-delete query on the MS2FileScanCharge and
     * MS2FileChargeDependentAnalysis tables.
     * @param scanId
     */
    public abstract void deleteByScanIdCascade(int scanId);

}