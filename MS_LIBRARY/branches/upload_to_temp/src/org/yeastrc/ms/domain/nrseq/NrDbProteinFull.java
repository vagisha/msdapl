package org.yeastrc.ms.domain.nrseq;

public class NrDbProteinFull extends NrDbProtein {

    private int sequenceId;
    private int speciesId;
    
    public int getSequenceId() {
        return sequenceId;
    }
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }
    public int getSpeciesId() {
        return speciesId;
    }
    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }
}
