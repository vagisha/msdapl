package org.yeastrc.ms.dao;

import org.yeastrc.ms.MsExperiment;

public interface MsExperimentDAO {

    public abstract MsExperiment load(int msExperimentId);

    public abstract int save(MsExperiment experiment);

    public abstract void update(MsExperiment experiment);

}