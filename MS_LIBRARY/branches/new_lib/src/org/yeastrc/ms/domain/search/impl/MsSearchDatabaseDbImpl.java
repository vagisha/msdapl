/**
 * MsSearchDatabaseDbImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.io.File;

import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;

/**
 * 
 */
public class MsSearchDatabaseDbImpl implements MsSearchDatabaseDb {

    private int id;
    private int sequenceDatabaseId;
    private String serverAddress;
    private String serverPath;
    
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
    
    public String getServerAddress() {
        return serverAddress;
    }
    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public String getServerPath() {
        return serverPath;
    }
    /**
     * @param serverPath the serverPath to set
     */
    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
    
    @Override
    public String getDatabaseFileName() {
        if (serverPath != null)
            return new File(serverPath).getName();
        return null;
    }
    @Override
    public int getSequenceDatabaseId() {
        return sequenceDatabaseId;
    }
    
    public void setSequenceDatabaseId(int sequenceDatabaseId) {
        this.sequenceDatabaseId = sequenceDatabaseId;
    }
    
}
