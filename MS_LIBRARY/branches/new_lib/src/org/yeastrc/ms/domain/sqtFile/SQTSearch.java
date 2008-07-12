package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.MsSearch;

public interface SQTSearch extends MsSearch {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<? extends SQTField> getHeaders();

}