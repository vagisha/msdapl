package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class PeptideEvidence <T extends SpectrumMatch>{
    
    private Peptide peptide;
    private List<T> spectrumMatchList;
    
    public PeptideEvidence(Peptide peptide) {
        this.peptide = peptide;
        spectrumMatchList = new ArrayList<T>();
    }
    
    public PeptideEvidence(Peptide peptide, List<T> spectrumMatchList) {
        this(peptide);
        if (spectrumMatchList != null)
            this.spectrumMatchList = spectrumMatchList;
    }
    
    public void addSpectrumMatch(T spectrumMatch) {
        spectrumMatchList.add(spectrumMatch);
    }
    
    public void addSpectrumMatchList(List<T> spectrumMatchList) {
        spectrumMatchList.addAll(spectrumMatchList);
    }
    
    public List<T> getSpectrumMatchList() {
        return spectrumMatchList;
    }
    
    /**
     * Returns the sequence of the peptide without modifications;
     * @return
     */
    public String getPeptideSeq() {
        return peptide.getSequence();
    }
    
    public Peptide getPeptide() {
        return peptide;
    }
    
    public int getMatchSpectraCount() {
        return spectrumMatchList.size();
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString()+"\n");
        for(T psm: spectrumMatchList) {
            buf.append("\t"+psm.toString()+"\n");
        }
        return buf.toString();
    }
    
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || (o.getClass() != this.getClass()))
            return false;
        PeptideEvidence<?> that = (PeptideEvidence<?>) o;
        return this.peptide.getSequence().equals(that.peptide.getSequence());
    }
    
    public int hashCode() {
        return peptide.getSequence().hashCode();
    }
}
