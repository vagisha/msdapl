package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.MsPeakData;
import org.yeastrc.ms.domain.MsScan;

public class MsScanSqlMapParam implements MsScan {

    private int runId;
    private int precursorScanId;
    private MsScan scan;
    
    public MsScanSqlMapParam(int runId, int precursorScanId, MsScan scan) {
        this.runId = runId;
        this.precursorScanId = precursorScanId;
        this.scan = scan;
    }
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }
    
    public int getPrecursorScanId() {
        return precursorScanId;
    }
    
    public int getEndScanNum() {
        return scan.getEndScanNum();
    }

    public String getFragmentationType() {
        return scan.getFragmentationType();
    }

    public int getMsLevel() {
        return scan.getMsLevel();
    }

    public MsPeakData getPeaks() {
        return scan.getPeaks();
    }

    public BigDecimal getPrecursorMz() {
        return scan.getPrecursorMz();
    }

    public int getPrecursorScanNum() {
        return scan.getPrecursorScanNum();
    }

    public BigDecimal getRetentionTime() {
        return scan.getRetentionTime();
    }

    public int getStartScanNum() {
        return scan.getStartScanNum();
    }
}
