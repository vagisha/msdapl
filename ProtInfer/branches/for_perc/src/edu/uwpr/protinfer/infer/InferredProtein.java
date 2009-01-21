package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InferredProtein <T extends SpectrumMatch> {

    private double score;
    
    private Protein protein;
    
    private Map<Integer, PeptideEvidence<T>> peptideEvList;
    
    private float percentCoverage;
    
    public InferredProtein(Protein protein) {
        this.protein = protein;
        peptideEvList = new HashMap<Integer, PeptideEvidence<T>>();
    }
    
    public void addPeptideEvidence(PeptideEvidence<T> peptideEv) {
        PeptideEvidence<T> evidence = peptideEvList.get(peptideEv.getPeptide().getId());
        if (evidence == null) {
            this.peptideEvList.put(peptideEv.getPeptide().getId(), peptideEv);
        }
    }
    
    public PeptideEvidence<T> getPeptideEvidence(Peptide peptide) {
        return peptideEvList.get(peptide.getId());
    }
    
    public int getPeptideEvidenceCount() {
        return peptideEvList.size();
    }
    
    public int getSpectralEvidenceCount() {
        int count = 0;
        for(PeptideEvidence<T> ev: peptideEvList.values()) {
            count += ev.getSpectrumMatchCount();
        }
        return count;
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
    
    public float getPercentCoverage() {
        return percentCoverage;
    }

    public void setPercentCoverage(float percentCoverage) {
        this.percentCoverage = percentCoverage;
    }
    
    public String getDescription() {
        return protein.getDescription();
    }

    public void setDescription(String description) {
       protein.setDescription(description);
    }
}
