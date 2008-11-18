package edu.uwpr.protinfer.database.dto;

import java.sql.Date;
import java.util.List;

public class ProteinferRun {
    
    private int id;
    private Date dateCreated;
    private Date dateCompleted;
    private ProteinferStatus status;
    
    private int unfilteredProteins;
    
    private List<ProteinferFilter> filters;
    private List<ProteinferInput> inputSummaryList;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date date) {
        this.dateCreated = date;
    }
    
    public Date getDateCompleted() {
        return dateCompleted;
    }
    public void setDateCompleted(Date date) {
        this.dateCompleted = date;
    }
    
    public ProteinferStatus getStatus() {
        return status;
    }
    public boolean isComplete() {
        return status == ProteinferStatus.COMPLETE;
    }
    public void setStatus(ProteinferStatus status) {
        this.status = status;
    }
    
    public List<ProteinferFilter> getFilters() {
        return filters;
    }
    public void setFilters(List<ProteinferFilter> filter) {
        this.filters = filter;
    }
    
    public List<ProteinferInput> getInputSummaryList() {
        return inputSummaryList;
    }
    public void setInputSummaryList(List<ProteinferInput> inputList) {
        this.inputSummaryList = inputList;
    }
    
    public int getUnfilteredProteins() {
        return unfilteredProteins;
    }
    public void setUnfilteredProteins(int unfilteredProteins) {
        this.unfilteredProteins = unfilteredProteins;
    }
}
