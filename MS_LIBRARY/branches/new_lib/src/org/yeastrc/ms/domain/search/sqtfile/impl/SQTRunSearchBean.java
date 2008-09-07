package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.MsRunSearchBean;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;

public class SQTRunSearchBean extends MsRunSearchBean implements SQTRunSearch {

    private List<SQTHeaderItem> headers;
    
    public SQTRunSearchBean() {
        headers = new ArrayList<SQTHeaderItem>();
    }

    
    public List<SQTHeaderItem> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List<SQTHeaderItem> headers) {
        this.headers = headers;
    }
    
}
