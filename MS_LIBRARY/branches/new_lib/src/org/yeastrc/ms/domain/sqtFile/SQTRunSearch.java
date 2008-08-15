package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;

public interface SQTRunSearch extends MsRunSearch {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTField> getHeaders();

}