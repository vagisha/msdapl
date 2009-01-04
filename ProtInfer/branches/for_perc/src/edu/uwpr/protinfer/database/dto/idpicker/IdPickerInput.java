package edu.uwpr.protinfer.database.dto.idpicker;

import edu.uwpr.protinfer.database.dto.ProteinferInput;

public class IdPickerInput extends ProteinferInput {

    private int numTargetHits = -1;
    private int numDecoyHits = -1;
    private int numFilteredTargetHits = -1;
    
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
