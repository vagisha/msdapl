package edu.uwpr.protinfer.database.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class BaseProteinferRun<T extends ProteinferInput> {

    private int id;
    private Date date;
//    private ProteinferStatus status;
    private ProteinInferenceProgram program;
    private String comments;
    private List<T> inputSummaryList;

    public BaseProteinferRun() {
        inputSummaryList = new ArrayList<T>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

//    public ProteinferStatus getStatus() {
//        return status;
//    }
//    
//    public String getStatusString() {
//        return String.valueOf(status.getStatusChar());
//    }
//
//    public boolean isComplete() {
//        return status == ProteinferStatus.COMPLETE;
//    }
//
//    public void setStatus(ProteinferStatus status) {
//        this.status = status;
//    }

    public List<T> getInputSummaryList() {
        return inputSummaryList;
    }

    public void setInputSummaryList(List<T> inputList) {
        this.inputSummaryList = inputList;
    }

    public ProteinInferenceProgram getProgram() {
        return this.program;
    }

    public String getProgramString() {
        return program.getName();
    }
    
    public void setProgram(ProteinInferenceProgram program) {
        this.program = program;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}