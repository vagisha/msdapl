package org.yeastrc.ms.dto.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dto.MsPeptideSearch;

public class SQTPeptideSearch extends MsPeptideSearch {

    private List<SQTSearchHeader> headers;
    
    public SQTPeptideSearch() {
        headers = new ArrayList<SQTSearchHeader>();
    }

    /**
     * @return the headers
     */
    public List<SQTSearchHeader> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List<SQTSearchHeader> headers) {
        this.headers = headers;
    }
    
    public void addHeader(SQTSearchHeader header) {
        headers.add(header);
    }
}
