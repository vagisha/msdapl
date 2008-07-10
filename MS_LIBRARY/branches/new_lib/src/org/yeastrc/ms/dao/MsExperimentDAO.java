package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.IMsExperiment;

public interface MsExperimentDAO {

    public abstract IMsExperiment load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract int save(IMsExperiment experiment);

    public abstract void delete(int msExperimentId);

}