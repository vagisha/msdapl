package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.util.Iterator;

import org.yeastrc.ms.dto.Peaks.Peak;

public interface IMsScan {

    /**
     * @return the runId
     */
    public abstract int getRunId();

    /**
     * @param runId the runId to set
     */
    public abstract void setRunId(int runId);

    /**
     * @return the id
     */
    public abstract int getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(int id);

    /**
     * @return the startScanNum
     */
    public abstract int getStartScanNum();

    /**
     * @param startScanNum the startScanNum to set
     */
    public abstract void setStartScanNum(int startScanNum);

    /**
     * @return the endScanNum
     */
    public abstract int getEndScanNum();

    /**
     * @param endScanNum the endScanNum to set
     */
    public abstract void setEndScanNum(int endScanNum);

    /**
     * @return the msLevel
     */
    public abstract int getMsLevel();

    /**
     * @param msLevel the msLevel to set
     */
    public abstract void setMsLevel(int msLevel);

    /**
     * @return the fragmentationType
     */
    public abstract String getFragmentationType();

    /**
     * @param fragmentationType the fragmentationType to set
     */
    public abstract void setFragmentationType(String fragmentationType);

    /**
     * @return the retentionTime
     */
    public abstract BigDecimal getRetentionTime();

    /**
     * @param retentionTime the retentionTime to set
     */
    public abstract void setRetentionTime(BigDecimal retentionTime);

    /**
     * @return the precursorMz
     */
    public abstract BigDecimal getPrecursorMz();

    /**
     * @param precursorMz the precursorMz to set
     */
    public abstract void setPrecursorMz(BigDecimal precursorMz);

    /**
     * @return the precursorScanId
     */
    public abstract int getPrecursorScanId();

    /**
     * @param precursorScanId the precursorScanId to set
     */
    public abstract void setPrecursorScanId(int precursorScanId);

    /**
     * @param peaks the peaks to set
     */
    public abstract void setPeaks(Peaks peaks);

    public abstract Peaks getPeaks();

    public abstract void setPeaksBinary(byte[] peakData) throws Exception;

    public abstract byte[] getPeaksBinary();

    public abstract Iterator<Peak> getPeaksIterator();

    /**
     * @return the precursorScanNum
     */
    public abstract int getPrecursorScanNum();

    /**
     * @param precursorScanNum the precursorScanNum to set
     */
    public abstract void setPrecursorScanNum(int precursorScanNum);

}