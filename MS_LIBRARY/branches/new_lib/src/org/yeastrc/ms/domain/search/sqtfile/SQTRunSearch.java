package org.yeastrc.ms.domain.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearch;

public interface SQTRunSearch extends MsRunSearch {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTField> getHeaders();

}