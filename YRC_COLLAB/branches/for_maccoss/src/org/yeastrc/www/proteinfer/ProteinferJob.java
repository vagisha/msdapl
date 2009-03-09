package org.yeastrc.www.proteinfer;

import org.yeastrc.jobqueue.Job;

public class ProteinferJob extends Job {

    private int pinferId;
    private String program;
    private String comments;
    
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public int getPinferId() {
        return pinferId;
    }
    public void setPinferRunId(int pinferId) {
        this.pinferId = pinferId;
    }
    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }
}
