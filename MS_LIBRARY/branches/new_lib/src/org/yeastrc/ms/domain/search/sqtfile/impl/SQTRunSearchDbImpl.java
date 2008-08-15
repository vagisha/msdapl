package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.MsRunSearchDbImpl;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class SQTRunSearchDbImpl extends MsRunSearchDbImpl implements SQTRunSearchDb {

    private List<? super SQTHeaderDb> headers;
    
    public SQTRunSearchDbImpl() {
        headers = new ArrayList<SQTHeaderDb>();
    }

    
    public List<SQTHeaderDb> getHeaders() {
        return (List<SQTHeaderDb>) headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List<? super SQTHeaderDb> headers) {
        this.headers = headers;
    }
    
    public void addHeader(SQTHeaderDb header) {
        headers.add(header);
    }
}
