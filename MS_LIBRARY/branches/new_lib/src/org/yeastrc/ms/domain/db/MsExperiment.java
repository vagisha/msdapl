/**
 * Experiment.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.db;

import java.sql.Date;

import org.yeastrc.ms.domain.IMsExperiment;

/**
 * @param <MsRun>
 * 
 */
public class MsExperiment implements IMsExperiment {

    private int id;
    private Date date;
    private String serverAddress;
    private String serverDirectory;
    
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
     * @return the serverDirectory
     */
    public String getServerDirectory() {
        return serverDirectory;
    }
    /**
     * @param directory the serverDirectory to set
     */
    public void setServerDirectory(String directory) {
        this.serverDirectory = directory;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
   
}
