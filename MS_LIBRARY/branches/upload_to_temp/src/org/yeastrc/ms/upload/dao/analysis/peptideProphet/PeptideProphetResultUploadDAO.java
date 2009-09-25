/**
 * PeptideProphetResultDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;

/**
 * 
 */
public interface PeptideProphetResultUploadDAO {

    public abstract PeptideProphetResult loadForProphetResultId(int peptideProphetResultId);
    
    public abstract PeptideProphetResult loadForRunSearchAnalysis(int searchResultId, int runSearchAnalysisId);
    
    public abstract PeptideProphetResult loadForSearchAnalysis(int searchResultId, int searchAnalysisId);
    
//    public abstract List<PeptideProphetResult> load(int resultId);
//    
//    public abstract PeptideProphetResult loadForAnalysis(int resultId, int searchAnalysisId);
//    
//    public abstract PeptideProphetResult load(int resultId, int runSearchAnalysisId);
    
    public abstract void saveAllPeptideProphetResultData(List<PeptideProphetResultDataWId> dataList);
    
}
