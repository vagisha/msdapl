/**
 * MsSequenceDatabase.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public class MsSequenceDatabase implements IMsSequenceDatabase {

    private int id;
    private String serverAddress;
    private String serverPath;
    private long sequenceLength;
    private int proteinCount;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSequenceDatabase#getServerAddress()
     */
    public String getServerAddress() {
        return serverAddress;
    }
    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSequenceDatabase#getServerPath()
     */
    public String getServerPath() {
        return serverPath;
    }
    /**
     * @param serverPath the serverPath to set
     */
    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSequenceDatabase#getSequenceLength()
     */
    public long getSequenceLength() {
        return sequenceLength;
    }
    /**
     * @param sequenceLength the sequenceLength to set
     */
    public void setSequenceLength(long sequenceLength) {
        this.sequenceLength = sequenceLength;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSequenceDatabase#getProteinCount()
     */
    public int getProteinCount() {
        return proteinCount;
    }
    /**
     * @param proteinCount the proteinCount to set
     */
    public void setProteinCount(int proteinCount) {
        this.proteinCount = proteinCount;
    }
    
    
}
