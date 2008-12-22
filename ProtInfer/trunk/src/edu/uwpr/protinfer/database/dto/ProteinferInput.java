package edu.uwpr.protinfer.database.dto;

public class ProteinferInput {

    private int id;
    private int pinferId;
    private int runSearchId;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
    
    public int getRunSearchId() {
        return runSearchId;
    }
    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
    }
}
