/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.general;

import java.util.List;

import org.yeastrc.ms.domain.general.MsExperiment;

/**
 * 
 */
public interface MsExperimentDAO {

    public MsExperiment loadExperiment(int experimentId);
    
    public List<Integer> getRunIdsForExperiment(int experimentId);
    
    public List<Integer> getExperimentIdsForRun(int runId);
    
    public abstract int saveExperiment(MsExperiment experiment);
    
    public abstract void saveExperimentRun(int experimentId, int runId);
    
    public abstract int getMatchingExptRunCount(int experimentId, int runId);
    
    public abstract void updateLastUpdateDate(int experimentId);
    
    public abstract void deleteExperiment(int experimentId);
}
