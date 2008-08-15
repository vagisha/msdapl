package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;

public interface MsExperimentDAO {

    public abstract MsExperimentDb load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract int save(MsExperiment experiment);
    
    public abstract void delete(int msExperimentId);
}