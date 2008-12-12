/**
 * MsPostSearchAnalysisWrap.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.postsearch.ibatis;

import org.yeastrc.ms.domain.postsearch.MsPostSearchAnalysisIn;
import org.yeastrc.ms.domain.search.SearchProgram;

/**
 * NOTE: This class is used internally by MsPostSearchAnalysisDAOImpl.
 */
public class MsPostSearchAnalysisWrap {

    private int searchId;
    private MsPostSearchAnalysisIn analysis;
    
    public MsPostSearchAnalysisWrap(int searchId, MsPostSearchAnalysisIn analysis) {
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

    public String getServerDirectory() {
        return analysis.getServerDirectory();
    }
}
