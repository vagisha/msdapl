/**
 * MsSequenceDatabase.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

/**
 * 
 */
public class MsSequenceDatabase {

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
    /**
     * @return the serverAddress
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
    /**
     * @return the serverPath
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
    /**
     * @return the sequenceLength
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
    /**
     * @return the proteinCount
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
