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

    public abstract void saveExperimentRun(int experimentId, int runId);

    public abstract void updateLastUpdateDate(int experimentId);
    
    public abstract void deleteExperiment(int experimentId);

}