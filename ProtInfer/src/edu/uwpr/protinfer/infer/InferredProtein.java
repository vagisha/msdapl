package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InferredProtein <T extends SpectrumMatch> {

    private double score;
    
    private Protein protein;
    
    private Map<String, PeptideEvidence<T>> peptideEvList;
    
    public InferredProtein(Protein protein) {
        this.protein = protein;
        peptideEvList = new HashMap<String, PeptideEvidence<T>>();
    }
    
    public void addPeptideEvidence(PeptideEvidence<T> peptideEv) {
        PeptideEvidence<T> evidence = peptideEvList.get(peptideEv.getModifiedPeptideSeq());
        if (evidence == null) {
            this.peptideEvList.put(peptideEv.getModifiedPeptideSeq(), peptideEv);
        }
    }
    
    public PeptideEvidence<T> getPeptideEvidence(Peptide peptide) {
        return peptideEvList.get(peptide.getModifiedSequence());
    }
    
    public int getPeptideEvidenceCount() {
        return peptideEvList.size();
    }
    
    public int getUniquePeptideEvidenceCount() {
        int cnt = 0;
        for(PeptideEvidence<T> pev: peptideEvList.values()) {
            if(pev.getProteinMatchCount() == 1)
                cnt++;
        }
        return cnt;
    }
    
    public int getSpectralEvidenceCount() {
        int count = 0;
        for(PeptideEvidence<T> ev: peptideEvList.values()) {
            count += ev.getSpectrumMatchCount();
        }
        return count;
    }
    
    /**
     * @param modifiedPeptideSequence -- modified sequence
     * @return
     */
    public PeptideEvidence<T> getPeptideEvidence(String modifiedPeptideSequence) {
        return peptideEvList.get(modifiedPeptideSequence);
    }
    
    public List<PeptideEvidence<T>> getPeptides() {
        List<PeptideEvidence<T>> list = new ArrayList<PeptideEvidence<T>>(peptideEvList.size());
        list.addAll(peptideEvList.values());
        return list;
    }
    
    public Protein getProtein() {
        return protein;
    }
    
    public int getProteinId() {
        return protein.getId();
    }
    
    public String getAccession() {
        return protein.getAccession();
    }

    public boolean getIsAccepted() {
        return protein.isAccepted();
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getProteinGroupId() {
        return protein.getProteinGroupId();
    }
    
    public int getProteinClusterId() {
        return protein.getProteinClusterId();
    }
}
