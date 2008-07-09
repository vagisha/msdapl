package org.yeastrc.ms.dto;

public interface IMsSequenceDatabase {

    /**
     * @return the serverAddress
     */
    public abstract String getServerAddress();

    /**
     * @return the serverPath
     */
    public abstract String getServerPath();

    /**
     * @return the sequenceLength
     */
    public abstract long getSequenceLength();

    /**
     * @return the proteinCount
     */
    public abstract int getProteinCount();

}