package edu.uwpr.protinfer.infer;

public class Protein {

    private final String accession;
    private int id; // could be a database id
    private boolean isDecoy = false;
    private boolean isAccepted = false;
    private int proteinGroupId;
    
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
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDecoy() {
        this.isDecoy = true;
    }
    
    public boolean isDecoy() {
        return isDecoy;
    }
    
    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public void setProteinGroupId(int proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }

    public int getProteinGroupId() {
        return proteinGroupId;
    }
    
    public String toString() {
        return accession+"\tID:"+id;
    }
}
