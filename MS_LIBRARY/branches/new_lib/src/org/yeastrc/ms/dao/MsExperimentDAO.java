package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsExperiment;

public interface MsExperimentDAO {

    public abstract MsExperiment load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract int save(MsExperiment experiment);

    public abstract void delete(int msExperimentId);

    public abstract void deleteAll();

}