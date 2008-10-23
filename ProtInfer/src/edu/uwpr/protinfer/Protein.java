package edu.uwpr.protinfer;

public class Protein {

    private final String accession;
    private int id; // could be a database id
    private boolean isDecoy = false;
    
    /**
     * @param accession
     * @param id unique id for this protein
     */
    public Protein(String accession, int id) {
        this.accession = accession;
        this.id = id;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public int getId() {
        return id;
    }
    
    public void setDecoy() {
        this.isDecoy = true;
    }
    
    public boolean isDecoy() {
        return isDecoy;
    }
    
    public String toString() {
        return accession+"\tID:"+id;
    }
}
