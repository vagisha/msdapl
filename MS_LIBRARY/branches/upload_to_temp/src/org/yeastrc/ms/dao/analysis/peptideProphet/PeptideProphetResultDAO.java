/**
 * PeptideProphetResultDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;

/**
 * 
 */
public interface PeptideProphetResultDAO {

    public abstract PeptideProphetResult load(int resultId);
    
    // ids for a runSearchAnalysis
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, 
                            PeptideProphetResultFilterCriteria filterCriteria, 
                            ResultSortCriteria sortCriteria);
    public abstract int numRunAnalysisResults(int runSearchAnalysisId);
    
    
    
    // ids for a searchAnalysis
    public abstract List<Integer> loadResultIdsForSearchAnalysis(int searchAnalysisId, 
            PeptideProphetResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadResultIdsForSearchAnalysisUniqPeptide(int searchAnalysisId, 
            PeptideProphetResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadResultIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId);
    
    public abstract List<Integer> loadResultIdsForAnalysis(int analysisId);
    
    public abstract List<Integer> loadResultIdsForAnalysis(int analysisId, int limit, int offset);
    
    public abstract int numAnalysisResults(int searchAnalysisId);
    
    
    
    public abstract void save(PeptideProphetResultDataWId data);
    
    public abstract void saveAllPeptideProphetResultData(List<PeptideProphetResultDataWId> dataList);
    
    
    public abstract void deleteResultsForRunSearchAnalysis(int id);
    
}
