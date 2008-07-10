package org.yeastrc.ms.domain.ms2File;

import java.util.List;

import org.yeastrc.ms.domain.IMsRun;

public interface IMS2Run extends IMsRun {

    public abstract List<? extends IHeader> getHeaderList();

}