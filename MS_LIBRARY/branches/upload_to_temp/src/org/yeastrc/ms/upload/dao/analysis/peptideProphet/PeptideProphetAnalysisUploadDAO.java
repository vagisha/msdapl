package org.yeastrc.ms.upload.dao.analysis.peptideProphet;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;

public interface PeptideProphetAnalysisUploadDAO {

    public PeptideProphetAnalysis load(int analysisId);
    
    public int save(PeptideProphetAnalysis analysis);
    
    public PeptideProphetAnalysis loadAnalysisForFileName(String fileName, int searchId);
}
