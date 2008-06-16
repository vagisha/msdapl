/**
 * Experiment.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <MsRun>
 * 
 */
public class Experiment {

    private int id;
    private String serverAddress;
    private String serverDir;
    
    private List <MsRun> runs; // runs for this experiments;
    
    public Experiment() {
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
     * @return the serverDir
     */
    public String getServerDir() {
        return serverDir;
    }
    /**
     * @param serverDir the serverDir to set
     */
    public void setServerDir(String serverDir) {
        this.serverDir = serverDir;
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
    
    
}
