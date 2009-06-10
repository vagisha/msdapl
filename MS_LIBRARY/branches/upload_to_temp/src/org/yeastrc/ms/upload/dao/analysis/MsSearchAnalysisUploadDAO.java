package org.yeastrc.ms.upload.dao.analysis;

import java.util.List;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

public interface MsSearchAnalysisUploadDAO {

    public abstract int save(MsSearchAnalysis analysis);
    
    public abstract List<Integer> getAnalysisIdsForSearch(int searchId);
    
    public abstract int updateAnalysisProgramVersion(int analysisId, String versionStr);
    
    public abstract int updateAnalysisProgram(int analysisId, Program program);
    
    public void delete(int analysisId);
    
}
