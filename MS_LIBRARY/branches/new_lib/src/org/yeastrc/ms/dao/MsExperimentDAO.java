package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;

public interface MsExperimentDAO {

    public abstract MsExperimentDb load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract List<Integer> selectExperimentIdsForRun(int runId);
    
    public abstract List<Integer> selectRunIdsForExperiment(int experimentId);
    
    public abstract int save(MsExperiment experiment);
    
    public abstract void saveRunExperiment(int experimentId, int runId);

    public abstract void delete(int msExperimentId);
}