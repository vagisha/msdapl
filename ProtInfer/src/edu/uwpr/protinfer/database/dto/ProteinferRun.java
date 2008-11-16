package edu.uwpr.protinfer.database.dto;

import java.sql.Date;
import java.util.List;

public class ProteinferRun {
    
    private int id;
    private Date dateCreated;
    private Date dateCompleted;
    private ProteinferStatus status;
    
    private List<ProteinferFilter> filters;
    private List<Integer> runSearchIds;
    
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
    
    public List<Integer> getRunSearchIds() {
        return runSearchIds;
    }
    public void setRunSearchIds(List<Integer> runSearchIds) {
        this.runSearchIds = runSearchIds;
    }
}
