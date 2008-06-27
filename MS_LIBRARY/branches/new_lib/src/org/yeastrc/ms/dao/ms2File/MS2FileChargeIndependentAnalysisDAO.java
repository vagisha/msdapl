package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.dto.ms2File.MS2FileChargeIndependentAnalysis;

public interface MS2FileChargeIndependentAnalysisDAO {

    public abstract List<MS2FileChargeIndependentAnalysis> loadAnalysisForScan(
            int scanId);

    public abstract boolean save(MS2FileChargeIndependentAnalysis analysis);

}