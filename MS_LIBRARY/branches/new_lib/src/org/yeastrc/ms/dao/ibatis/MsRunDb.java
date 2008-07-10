package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.domain.IMsRun;

public class MsRunDb {

    private int experimentId;
    private IMsRun run;
    
    public MsRunDb(int experimentId, IMsRun run) {
        this.experimentId = experimentId;
        this.run = run;
    }
    
    /**
     * @return the experimentId
     */
    public int getExperimentId() {
        return experimentId;
    }
    /**
     * @return the run
     */
    public IMsRun getRun() {
        return run;
    }
}
