/**
 * MsPostSearchAnalysisWrap.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.ibatis;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysisIn;
import org.yeastrc.ms.domain.search.SearchProgram;

/**
 * NOTE: This class is used internally by MsPostSearchAnalysisDAOImpl.
 */
public class MsSearchAnalysisWrap {

    private int searchId;
    private MsSearchAnalysisIn analysis;
    
    public MsSearchAnalysisWrap(int searchId, MsSearchAnalysisIn analysis) {
        this.searchId = searchId;
        this.analysis = analysis;
    }

    public SearchProgram getAnalysisProgram() {
        return analysis.getAnalysisProgram();
    }

    public String getAnalysisProgramVersion() {
        return analysis.getAnalysisProgramVersion();
    }

    public int getSearchId() {
        return searchId;
    }
}
