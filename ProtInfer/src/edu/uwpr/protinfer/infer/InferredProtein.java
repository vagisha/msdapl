package edu.uwpr.protinfer.infer;

import java.util.HashMap;
import java.util.Map;


public class InferredProtein <T extends SpectrumMatch> {

    private boolean accepted;
    private double score;
    private int proteinGroupId;
    
    private Protein protein;
    
    private Map<String, PeptideEvidence<T>> peptideEvList;
    
    public InferredProtein(Protein protein) {
        this.protein = protein;
        peptideEvList = new HashMap<String, PeptideEvidence<T>>();
    }
    
    public void addPeptideEvidence(PeptideEvidence<T> peptideEv) {
        if (peptideEvList.containsKey(peptideEv.getPeptideSeq())) {
            PeptideEvidence<T> evidence = peptideEvList.get(peptideEv.getPeptideSeq());
            evidence.addSpectrumMatchList(evidence.getSpectrumMatchList());
        }
        else {
            this.peptideEvList.put(peptideEv.getPeptideSeq(), peptideEv);
        }
    }
    
    public PeptideEvidence<T> getPeptideEvidence(Peptide peptide) {
        return peptideEvList.get(peptide.getSequence());
    }
    
    public PeptideEvidence<T> getPeptideEvidence(String peptideSequence) {
        return peptideEvList.get(peptideSequence);
    }
    
    public Protein getProtein() {
        return protein;
    }
    
    public String getAccession() {
        return protein.getAccession();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getProteinGroupId() {
        return proteinGroupId;
    }

    public void setProteinGroupId(int proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }
}
