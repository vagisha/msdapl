/**
 * MsRunLocationDbImpl.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.run.MsRunLocation;

/**
 * 
 */
public class RunLocationBean implements MsRunLocation {

    private int id;                 // database id for this location
    private int runId;              // database id for the locations's run
    private String serverAddress;    // server hosting this location
    private String serverDirectory; 
    private Date createDate;
    
    @Override
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getRunId() {
        return runId;
    }
    
    public void setRunId(int runId) {
        this.runId = runId;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }
    
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String getServerDirectory() {
        return serverDirectory;
    }
    
    public void setServerDirectory(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Date date) {
        this.createDate = date;
    }
}
