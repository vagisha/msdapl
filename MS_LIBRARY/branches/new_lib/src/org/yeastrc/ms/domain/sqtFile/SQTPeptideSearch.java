package org.yeastrc.ms.domain.sqtFile;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsPeptideSearch;

public class SQTPeptideSearch extends MsPeptideSearch implements ISQTPeptideSearch {

    private List<SQTSearchHeader> headers;
    
    public SQTPeptideSearch() {
        headers = new ArrayList<SQTSearchHeader>();
    }

    public SQTPeptideSearch(MsPeptideSearch search) {
        this();
        setRunId(search.getRunId());
        setOriginalFileType(search.getOriginalFileType());
        setSearchEngineName(search.getSearchEngineName());
        setSearchEngineVersion(search.getSearchEngineVersion());
        setSearchDate(search.getSearchDate());
        setSearchDuration(search.getSearchDuration());
        setPrecursorMassType(search.getPrecursorMassType());
        setPrecursorMassTolerance(search.getPrecursorMassTolerance());
        setFragmentMassType(search.getFragmentMassType());
        setFragmentMassTolerance(search.getFragmentMassTolerance());
    }
    
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
