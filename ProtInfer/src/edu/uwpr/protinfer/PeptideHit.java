package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.List;

public class PeptideHit {

    private Peptide peptide;
    private List<ProteinHit> proteins;
    
    public PeptideHit(String peptide) {
        this.peptide = new Peptide(peptide);
        proteins = new ArrayList<ProteinHit>();
    }
    
    public PeptideHit(String peptide, List<ProteinHit> proteins) {
        this(peptide);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    /**
     * Returns the sequence of the peptide with modifications. E.g. PEP(80.0)TIDE
     * @return
     */
    public String getPeptideSeq() {
        return peptide.getPeptideSeq();
    }
    
    /**
     * Returns the unmodified sequence of the peptide
     * @return
     */
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
    
    public void addModification(PeptideModification modification) {
        this.peptide.addModification(modification);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString()+"\n");
        for(ProteinHit protHit: proteins) {
            buf.append("\t"+protHit+"\n");
        }
        return buf.toString();
    }
}
