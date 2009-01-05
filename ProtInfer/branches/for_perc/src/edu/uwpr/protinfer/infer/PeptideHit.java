package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class PeptideHit {

    private Peptide peptide;
    private List<ProteinHit> proteins;
    
    public PeptideHit(Peptide peptide) {
        this.peptide = peptide;
        proteins = new ArrayList<ProteinHit>();
    }
    
    public PeptideHit(Peptide peptide, List<ProteinHit> proteins) {
        this(peptide);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    public Peptide getPeptide() {
        return peptide;
    }
    
//    public String getModifiedSequence() {
//        return peptide.getModifiedSequence();
//    }
    
    public String getSequence() {
        return peptide.getSequence();
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
    
    public boolean isDecoyPeptide() {
        for (ProteinHit protHit: getProteinList()) {
            if (!protHit.getProtein().isDecoy())
                return false;
        }
        return true;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.getSequence()+"\n");
        for(ProteinHit protHit: proteins) {
            buf.append("\t"+protHit+"\n");
        }
        return buf.toString();
    }
}
