/**
 * PercolatorSearch.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis;

import java.sql.Date;

import org.yeastrc.ms.domain.search.SearchProgram;

/**
 * 
 */
public interface MsPostSearchAnalysis {

    /**
     * @return the database id for this Percolator Analysis
     */
    public abstract int getId();
    
    /**
     * database id of the search on which Percolator was run.
     * @return
     */
    public abstract int getSearchId();
    
    /**
     * @return the date this search was uploaded
     */
    public abstract Date getUploadDate();
    
    
    /**
     * @return the serverDirectory
     */
    public abstract String getServerDirectory();

    
    /**
     * @return the analysisProgramName
     */
    public abstract SearchProgram getAnalysisProgram();

    /**
     * @return the analysisProgramVersion
     */
    public abstract String getAnalysisProgramVersion();
    
}
