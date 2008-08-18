package org.yeastrc.ms.domain.search.sequest;

import org.yeastrc.ms.domain.search.MsRunSearchResult;

public interface SequestRunSearchResult extends MsRunSearchResult {
    
    public SequestResultData getSequestResultData();
}
