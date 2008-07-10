package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.domain.IMsScan;

public class MsScanDb {

    private int runId;
    private IMsScan scan;
    
    public MsScanDb(int runId, IMsScan scan) {
        this.runId = runId;
        this.scan = scan;
    }
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }
    /**
     * @return the scan
     */
    public IMsScan getScan() {
        return scan;
    }
}
