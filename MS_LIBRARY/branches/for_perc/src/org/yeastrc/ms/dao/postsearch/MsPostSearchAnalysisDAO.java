package org.yeastrc.ms.dao.postsearch;

import java.util.List;

import org.yeastrc.ms.domain.postsearch.MsPostSearchAnalysis;
import org.yeastrc.ms.domain.postsearch.MsPostSearchAnalysisIn;
import org.yeastrc.ms.domain.search.SearchProgram;

public interface MsPostSearchAnalysisDAO {

    public abstract int save(MsPostSearchAnalysisIn analysis, int searchId);
    
    public abstract MsPostSearchAnalysis load(int analysisId);
    
    public abstract List<Integer> getAnalysisIdsForSearch(int searchId);
    
    public abstract int updateAnalysisProgramVersion(int searchId, String versionStr);
    
    public abstract int updateAnalysisProgram(int searchId, SearchProgram program);
    
    public void delete(int analysisId);
    
}
