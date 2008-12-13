package org.yeastrc.ms.domain.analysis.percolator;

import org.yeastrc.ms.domain.search.SearchFileFormat;

public interface PercolatorOutputIn {

 
    public abstract String getFileName();
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getSearchFileFormat();
    
}
