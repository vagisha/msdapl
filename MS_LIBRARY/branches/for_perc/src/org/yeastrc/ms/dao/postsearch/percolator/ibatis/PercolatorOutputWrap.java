package org.yeastrc.ms.dao.postsearch.percolator.ibatis;

import org.yeastrc.ms.domain.postsearch.percolator.PercolatorOutputIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;

public class PercolatorOutputWrap {

    private int percolatorId;
    private int runSearchId;
    private PercolatorOutputIn output;
    
    public PercolatorOutputWrap(PercolatorOutputIn output, int percId, int runSearchId) {
        this.percolatorId = percId;
        this.output = output;
    }

    public SearchFileFormat getAnalysisFileFormat() {
        return output.getSearchFileFormat();
    }

    public int getRunSearchId() {
        return runSearchId;
    }

    public int getPercolatorId() {
        return percolatorId;
    }
}
