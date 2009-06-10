package org.yeastrc.ms.upload.dao.analysis.percolator;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;

public interface PercolatorParamsUploadDAO {

    public abstract void saveParam(PercolatorParam param, int analysisId);
    
}
