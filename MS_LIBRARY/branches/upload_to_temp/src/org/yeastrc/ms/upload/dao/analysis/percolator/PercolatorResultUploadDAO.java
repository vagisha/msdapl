package org.yeastrc.ms.upload.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public interface PercolatorResultUploadDAO {
    
    public abstract void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList);
    
}
