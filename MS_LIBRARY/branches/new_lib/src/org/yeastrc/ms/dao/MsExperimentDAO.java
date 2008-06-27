package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsExperiment;

public interface MsExperimentDAO {

    public abstract MsExperiment load(int msExperimentId);

    public abstract int save(MsExperiment experiment);

}