/**
 * PercolatorOutputBean.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator.impl;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorOutput;
import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public class PercolatorOutputBean implements PercolatorOutput {

    private int id;
    private int percolatorId;
    private int runSearchId;
    private SearchFileFormat fileFormat;
    
    
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public int getPercolatorId() {
        return percolatorId;
    }
    
    public void setPercolatorId(int percId) {
        this.percolatorId = percId;
    }
    
    @Override
    public int getRunSearchId() {
        return runSearchId;
    }

    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
    }
    
    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return fileFormat;
    }

    public void setAnalysisFileFormat(SearchFileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }
}
