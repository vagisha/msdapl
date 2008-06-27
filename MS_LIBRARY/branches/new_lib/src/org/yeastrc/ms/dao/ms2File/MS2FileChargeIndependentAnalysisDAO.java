package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.Ms2FileChargeIndependentAnalysis;

public interface MS2FileChargeIndependentAnalysisDAO {

    public abstract List<Ms2FileChargeIndependentAnalysis> loadAnalysisForScan(
            int scanId);

    public abstract boolean save(Ms2FileChargeIndependentAnalysis analysis);

}