package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.List;

public class PeptideHit {

    private final String peptide;
    private final String label;
    private List<ProteinHit> proteins;
    
    public PeptideHit(String peptide, String label) {
        this.peptide = peptide;
        this.label = label;
        proteins = new ArrayList<ProteinHit>();
    }
    
    public PeptideHit(String peptide, String label, List<ProteinHit> proteins) {
        this(peptide, label);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    public String getPeptide() {
        return peptide;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void addProteinHit(ProteinHit hit) {
        this.proteins.add(hit);
    }
    
    public List<ProteinHit> getProteinList() {
        return proteins;
    }
}
