package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;

public interface MS2ChargeIndependentAnalysisDAO {

    public abstract List<MS2ChargeIndependentAnalysisDb> loadAnalysisForScan(int scanId);

    public abstract void save(MS2Field analysis, int scanId);
    
    public abstract void saveAll(List<MS2ChargeIndependentAnalysisDb> analysisList);
    
    public abstract void deleteByScanId(int scanId);

}