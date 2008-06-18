/**
 * Experiment.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <MsRun>
 * 
 */
public class MsExperiment {

    private int id;
    private Date date;
    private String serverAddress;
    private String serverDirectory;
    
    private List <MsRun> runs; // runs for this experiments;
    
    public MsExperiment() {
        runs = new ArrayList<MsRun>();
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
     * @return the runs
     */
    public List<MsRun> getRuns() {
        return runs;
    }

    /**
     * @param runs the runs to set
     */
    public void setRuns(List<MsRun> runs) {
        this.runs = runs;
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
