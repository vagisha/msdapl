package org.yeastrc.ms.domain.search.sqtfile.impl;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItemIn;

public class SQTHeaderItemImpl implements SQTHeaderItem {

    private int runSearchId;
    private SQTHeaderItemIn headerItem;
    
    public SQTHeaderItemImpl(SQTHeaderItemIn headerItem, int runSearchId) {
        this.headerItem = headerItem;
        this.runSearchId = runSearchId;
    }
    
    public int getId() {
        throw new UnsupportedOperationException();
    }
   
    public int getRunSearchId() {
        return runSearchId;
    }
    
    
    public String getName() {
        return headerItem.getName();
    }

    public String getValue() {
        return headerItem.getValue();
    }
}
