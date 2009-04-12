/**
 * ProteinferRunPlus.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.Date;

import edu.uwpr.protinfer.database.dto.ProteinferRun;

/**
 * 
 */
public class ProteinferRunFormBean {

    private int projectId;
    private int runId;
    private Date runDate;
    private String comments;
    private String inputGeneratorProgram;
    private boolean isSelected = false;
    

    public ProteinferRunFormBean() {}
    
    public ProteinferRunFormBean(ProteinferRun run, int projectId) {
        this.projectId = projectId;
        this.runId = run.getId();
        this.runDate = run.getDate();
        this.comments = run.getComments();
        this.inputGeneratorProgram = run.getInputGeneratorString();
    }
    
    
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setInputGeneratorProgram(String inputGeneratorProgram) {
        this.inputGeneratorProgram = inputGeneratorProgram;
    }

    public int getRunId() {
        return runId;
    }

    public int getProjectId() {
        return this.projectId;
    }
    
    public Date getRunDate() {
        return runDate;
    }

    public String getInputGeneratorProgram() {
        return inputGeneratorProgram;
    }
    
    
    public String getComments() {
        return comments;
    }
    
}
