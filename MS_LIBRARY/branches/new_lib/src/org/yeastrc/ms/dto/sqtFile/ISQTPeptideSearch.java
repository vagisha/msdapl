package org.yeastrc.ms.dto.sqtFile;

import java.util.List;

import org.yeastrc.ms.dto.IMsPeptideSearch;
import org.yeastrc.ms.dto.ms2File.IHeader;

public interface ISQTPeptideSearch extends IMsPeptideSearch {

    public abstract List<? extends IHeader> getHeaders();

}