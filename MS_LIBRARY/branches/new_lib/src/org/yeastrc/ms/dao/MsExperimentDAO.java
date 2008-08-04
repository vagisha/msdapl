package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;

public interface MsExperimentDAO {

    public abstract MsExperimentDb load(int msExperimentId);

    public abstract List<Integer> selectAllExperimentIds();
    
    public abstract List<Integer> loadExperimentIdsForRun(int runId);
    
    public abstract List<Integer> loadRunIdsForExperiment(int experimentId);
    
    /**
     * Returns the list of runIds that are quique to the given experiment
     * @param msExperimentId
     * @return
     */
    public abstract List<Integer> loadRunIdsUniqueToExperiment(int msExperimentId);
    
    
    public abstract int save(MsExperiment experiment);
    
    public abstract void saveRunExperiment(int experimentId, int runId);

    public abstract void delete(int msExperimentId);
}