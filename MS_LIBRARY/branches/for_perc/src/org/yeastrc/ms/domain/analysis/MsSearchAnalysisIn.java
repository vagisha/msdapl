/**
 * PercolatorSearchIn.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis;

import org.yeastrc.ms.domain.search.SearchProgram;


/**
 * 
 */
public interface MsSearchAnalysisIn {

    /**
     * @return the analysisProgramName
     */
    public abstract SearchProgram getAnalysisProgram();

    /**
     * @return the analysisProgramVersion
     */
    public abstract String getAnalysisProgramVersion();
    
}
