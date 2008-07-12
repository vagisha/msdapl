package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.ms2File.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

public interface MS2ChargeIndependentAnalysisDAO {

    public abstract List<MS2ChargeIndependentAnalysisDb> loadAnalysisForScan(int scanId);

    public abstract void save(MS2Field analysis, int scanId);
    
    public abstract void deleteByScanId(int scanId);

}