/**
 * MsSearchDatabaseBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.io.File;

import org.yeastrc.ms.domain.search.MsSearchDatabase;

/**
 * 
 */
public class MsSearchDatabaseBean extends SearchDatabase implements MsSearchDatabase {

    private int id;
    private int sequenceDatabaseId;
    
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
    
    @Override
    public int getSequenceDatabaseId() {
        return sequenceDatabaseId;
    }
    
    public void setSequenceDatabaseId(int sequenceDatabaseId) {
        this.sequenceDatabaseId = sequenceDatabaseId;
    }
    
    @Override
    public String getDatabaseFileName() {
        if (getServerPath() != null)
            return new File(getServerPath()).getName();
        return null;
    }
}
