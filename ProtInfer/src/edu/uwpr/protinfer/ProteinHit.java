package edu.uwpr.protinfer;

public class ProteinHit {

    private final String accession;
    private final String label;
    
    public ProteinHit(String accession, String label) {
        this.accession = accession;
        this.label = label;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public String getLabel() {
        return label;
    }
}
