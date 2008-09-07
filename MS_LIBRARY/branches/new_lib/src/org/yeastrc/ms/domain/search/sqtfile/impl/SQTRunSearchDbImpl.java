package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.MsRunSearchDbImpl;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class SQTRunSearchDbImpl extends MsRunSearchDbImpl implements SQTRunSearchDb {

    private List<? super SQTHeaderItem> headers;
    
    public SQTRunSearchDbImpl() {
        headers = new ArrayList<SQTHeaderItem>();
    }

    
    public List<SQTHeaderItem> getHeaders() {
        return (List<SQTHeaderItem>) headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List<? super SQTHeaderItem> headers) {
        this.headers = headers;
    }
    
    public void addHeader(SQTHeaderItem header) {
        headers.add(header);
    }
}
