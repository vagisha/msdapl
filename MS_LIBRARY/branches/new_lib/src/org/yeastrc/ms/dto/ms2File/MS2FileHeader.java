/**
 * Ms2FileHeaders.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

/**
 * 
 */
public class MS2FileHeader {

    private int id; // id(database) of the header
    private int runId;                  
    private String name;
    private String value;
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }
    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }
    /**
     * @return the header
     */
    public String getHeaderName() {
        return name;
    }
    /**
     * @param name the name of the header to set
     */
    public void setHeaderName(String name) {
        this.name = name;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
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
    
}
