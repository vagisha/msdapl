/**
 * MS2HeaderDbImpl.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import org.yeastrc.ms.domain.run.ms2file.MS2HeaderDb;


/**
 * 
 */
public class MS2HeaderDbImpl extends BaseHeader implements MS2HeaderDb {

    private int id; // id(database) of the header
    private int runId;                  
    
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

}
