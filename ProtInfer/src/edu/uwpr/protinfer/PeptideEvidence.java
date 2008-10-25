package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.List;

public class PeptideEvidence <T extends SpectrumMatch>{
    
    private Peptide peptide;
    private List<T> psmList;
    
    public PeptideEvidence(Peptide peptide) {
        this.peptide = peptide;
        psmList = new ArrayList<T>();
    }
    
    public PeptideEvidence(Peptide peptide, List<T> psmList) {
        this(peptide);
        if (psmList != null)
            this.psmList = psmList;
    }
    
    public void addSpectrumMatch(T psm) {
        psmList.add(psm);
    }
    
    public List<T> getSequenceMatchList() {
        return psmList;
    }
    
    /**
     * Returns the sequence of the peptide without modifications;
     * @return
     */
    public String getPeptideSeq() {
        return peptide.getSequence();
    }
    
    public int getMatchSpectraCount() {
        return psmList.size();
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString()+"\n");
        for(T psm: psmList) {
            buf.append("\t"+psm.toString()+"\n");
        }
        return buf.toString();
    }
}
