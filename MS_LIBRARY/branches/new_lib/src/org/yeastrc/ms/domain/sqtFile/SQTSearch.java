package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;

public interface SQTSearch extends MsSearch {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTField> getHeaders();

}