package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class InferredProtein <T extends SpectrumMatch> {

    private boolean accepted;
    private double score;
    private int proteinGroupId;
    
    private Protein protein;
    
    private List<PeptideEvidence> peptideEvList;
    
    public InferredProtein(Protein protein) {
        this.protein = protein;
        peptideEvList = new ArrayList<PeptideEvidence>();
    }
    
    public InferredProtein(Protein protein, List<PeptideEvidence> peptideEvList) {
        this(protein);
        if (peptideEvList != null)
            this.peptideEvList = peptideEvList;
    }
    
    public void addPeptideEvidence(PeptideEvidence peptideEv) {
        this.peptideEvList.add(peptideEv);
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
