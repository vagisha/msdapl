package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

public class PercolatorSQTHeaderWrap {

    private int percOutputId;
    private SQTHeaderItem header;
    
    public PercolatorSQTHeaderWrap(SQTHeaderItem header, int percOutputId) {
        this.header = header;
        this.percOutputId = percOutputId;
    }
   
    public String getName() {
        return header.getName();
    }
    
    public String getValue() {
        return header.getValue();
    }
    
    public int getPercolatorOutputId() {
        return percOutputId;
    }
}
