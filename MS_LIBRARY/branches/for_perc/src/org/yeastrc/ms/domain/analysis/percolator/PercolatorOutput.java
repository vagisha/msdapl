/**
 * PercolatorRunSearch.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator;

import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public interface PercolatorOutput {

    /**
     * @return database id of this percolator output
     */
    public abstract int getId();
    
    /**
     * @return database id of the original run_search that Percolator was run on.
     */
    public abstract int getRunSearchId();
    
    
    /**
     * @return database id of the percolator run that produced this output.
     */
    public abstract int getPercolatorId();
    
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getAnalysisFileFormat();
    
}
