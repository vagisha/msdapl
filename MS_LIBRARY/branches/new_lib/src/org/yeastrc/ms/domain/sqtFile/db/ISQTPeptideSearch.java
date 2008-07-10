package org.yeastrc.ms.domain.sqtFile.db;

import java.util.List;

import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.ms2File.db.IHeader;

public interface ISQTPeptideSearch extends IMsSearch {

    public abstract List<? extends IHeader> getHeaders();

}