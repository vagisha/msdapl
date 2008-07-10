package org.yeastrc.ms.dto.sqtFile;

import java.util.List;

import org.yeastrc.ms.dto.IMsSearch;
import org.yeastrc.ms.dto.ms2File.IHeader;

public interface ISQTPeptideSearch extends IMsSearch {

    public abstract List<? extends IHeader> getHeaders();

}