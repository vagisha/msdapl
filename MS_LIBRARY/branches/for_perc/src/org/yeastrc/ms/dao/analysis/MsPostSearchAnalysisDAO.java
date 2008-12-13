package org.yeastrc.ms.dao.analysis;

import java.util.List;

import org.yeastrc.ms.domain.analysis.MsPostSearchAnalysis;
import org.yeastrc.ms.domain.search.SearchProgram;

public interface MsPostSearchAnalysisDAO {

    public abstract int save(MsPostSearchAnalysis analysis);
    
    public abstract MsPostSearchAnalysis load(int analysisId);
    
    public abstract List<Integer> getAnalysisIdsForSearch(int searchId);
    
    public abstract int updateAnalysisProgramVersion(int analysisId, String versionStr);
    
    public abstract int updateAnalysisProgram(int analysisId, SearchProgram program);
    
    public void delete(int analysisId);
    
}
