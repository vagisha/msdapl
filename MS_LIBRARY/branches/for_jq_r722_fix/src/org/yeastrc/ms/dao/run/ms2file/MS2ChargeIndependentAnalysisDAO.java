package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

public interface MS2ChargeIndependentAnalysisDAO {

    public abstract List<MS2NameValuePair> loadAnalysisForScan(int scanId);

    public abstract void save(MS2NameValuePair analysis, int scanId);
    
    public abstract void saveAll(List<MS2ChargeIndependentAnalysisWId> analysisList);
    
    public abstract void deleteByScanId(int scanId);

}