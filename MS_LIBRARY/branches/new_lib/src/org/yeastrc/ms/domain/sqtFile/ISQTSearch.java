package org.yeastrc.ms.domain.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.ms2File.IHeader;

public interface ISQTSearch extends IMsSearch {

    public abstract List<? extends IHeader> getHeaders();

}