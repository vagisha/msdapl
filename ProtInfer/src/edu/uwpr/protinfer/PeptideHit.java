package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.List;

public class PeptideHit {

    private final String peptide;
    private final int id;
    private List<ProteinHit> proteins;
    
    public PeptideHit(String peptide, int id) {
        this.peptide = peptide;
        this.id = id;
        proteins = new ArrayList<ProteinHit>();
    }
    
    public PeptideHit(String peptide, int id, List<ProteinHit> proteins) {
        this(peptide, id);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    public String getPeptideSeq() {
        return peptide;
    }
    
    public String getLabel() {
        return String.valueOf(id);
    }
    
    public int getId() {
        return id;
    }
    
    public void addProteinHit(ProteinHit hit) {
        this.proteins.add(hit);
    }
    
    public List<ProteinHit> getProteinList() {
        return proteins;
    }
    
    public int getMatchProteinCount() {
        return proteins.size();
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide+"; id "+id+"\n");
        for(ProteinHit protHit: proteins) {
            buf.append(protHit+"\n");
        }
        return buf.toString();
    }
}
