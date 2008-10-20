package edu.uwpr.protinfer;

public class Protein {

    private final String accession;
    int id;
    
    public Protein(String accession, int id) {
        this.accession = accession;
        this.id = id;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public String getLabel() {
        return String.valueOf(id);
    }
    
    public int getId() {
        return id;
    }
    
    public String toString() {
        return id+"\t"+accession;
    }
}
