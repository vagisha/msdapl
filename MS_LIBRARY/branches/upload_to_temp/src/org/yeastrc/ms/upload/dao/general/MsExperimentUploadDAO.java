/**
 * MsExperimentUploadDAO.java
 * @author Vagisha Sharma
 * Jun 08, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.general;

import java.util.List;

import org.yeastrc.ms.domain.general.MsExperiment;

public interface MsExperimentUploadDAO {

    public abstract List<Integer> getAllExperimentIds();

    public abstract MsExperiment loadExperiment(int experimentId);

    public abstract int saveExperiment(MsExperiment experiment);

    /**
     * An entry is made in the msExperimentRun table if one does not already
     * exist for the given experimentId and runId
     * @param experimentId
     * @param runId
     */
    public abstract void saveExperimentRun(int experimentId, int runId);
    
    /**
     * An entry is made in the msExperimentRun table. 
     * If check == true the entry is created only if one does not already
     * exist with the given experimentId and runId
     * @param experimentId
     * @param runId
     * @param check
     */
    public abstract void saveExperimentRun(int experimentId, int runId, boolean check);

    public abstract void updateLastUpdateDate(int experimentId);
    
    public abstract boolean isExperimentRunLinked(int experimentId, int runId);
    
    public abstract void deleteExperiment(int experimentId);

}