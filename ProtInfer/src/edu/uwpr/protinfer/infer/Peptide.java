package edu.uwpr.protinfer.infer;


public class Peptide {

    private final String sequence;
    private final int id;
    
    /**
     * @param sequence
     * @param id unique id for this peptide
     */
    public Peptide(String sequence, int id) {
        this.sequence = sequence;
        this.id = id;
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public int getId() {
        return id;
    }
    
    public String toString() {
       return sequence;
    }
}
