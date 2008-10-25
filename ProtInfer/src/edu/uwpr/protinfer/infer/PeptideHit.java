package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class PeptideHit {

    private ModifiedPeptide peptide;
    private List<ProteinHit> proteins;
    
    public PeptideHit(ModifiedPeptide peptide) {
        this.peptide = peptide;
        proteins = new ArrayList<ProteinHit>();
    }
    
    public PeptideHit(ModifiedPeptide peptide, List<ProteinHit> proteins) {
        this(peptide);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    public ModifiedPeptide getModifiedPeptide() {
        return peptide;
    }
    
    public Peptide getPeptide() {
        return peptide.getPeptide();
    }
    
    public String getPeptideSequence() {
        return peptide.getPeptideSeq();
    }
    
    public String getUnmodifiedSequence() {
        return peptide.getUnmodifiedSequence();
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
        buf.append(peptide.getPeptideSeq()+"\n");
        for(ProteinHit protHit: proteins) {
            buf.append("\t"+protHit+"\n");
        }
        return buf.toString();
    }
}