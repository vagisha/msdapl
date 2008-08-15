package org.yeastrc.ms.domain.sqtFile.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.impl.MsSearchDbImpl;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;

public class SQTSearchDbImpl extends MsSearchDbImpl implements SQTSearchDb {

    private List<? super SQTHeaderDb> headers;
    
    public SQTSearchDbImpl() {
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
