/**
 * MsRunLocationWrap.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.impl;

import org.yeastrc.ms.domain.run.MsRunLocationIn;

/**
 * 
 */
public class MsRunLocationWrap {

    private int runId;
    private String serverAddress;
    private String serverDirectory;
    
    public MsRunLocationWrap (MsRunLocationIn runLoc, int runId) {
        this.serverAddress = runLoc.getServerAddress();
        this.serverDirectory = runLoc.getServerDirectory();
        this.runId = runId;
    }
    
    public MsRunLocationWrap(String serverAddress, String serverDirectory, int runId) {
        this.serverAddress = serverAddress;
        this.serverDirectory = serverDirectory;
        this.runId = runId;
    }
    
    public int getRunId() {
        return runId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getServerDirectory() {
        return serverDirectory;
    }
}
