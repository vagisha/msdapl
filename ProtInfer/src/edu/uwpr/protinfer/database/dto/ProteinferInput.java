package edu.uwpr.protinfer.database.dto;

public class ProteinferInput {

    private int pinferId;
    private int runSearchId;
    private int numTargetHits = -1;
    private int numDecoyHits = -1;
    private int numFilteredTargetHits = -1;
    
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
    
    public int getNumTargetHits() {
        return numTargetHits;
    }
    public void setNumTargetHits(int numTargetHits) {
        this.numTargetHits = numTargetHits;
    }
    
    public int getNumDecoyHits() {
        return numDecoyHits;
    }
    public void setNumDecoyHits(int numDecoyHits) {
        this.numDecoyHits = numDecoyHits;
    }
    
    public int getNumFilteredTargetHits() {
        return numFilteredTargetHits;
    }
    public void setNumFilteredTargetHits(int numFilteredTargetHits) {
        this.numFilteredTargetHits = numFilteredTargetHits;
    }
    
    
}
