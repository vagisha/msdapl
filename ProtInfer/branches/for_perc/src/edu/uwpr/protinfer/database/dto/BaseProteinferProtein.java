package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class BaseProteinferProtein<S extends ProteinferSpectrumMatch, T extends BaseProteinferPeptide<S>> {

    private int id;
    private int pinferId;
    private int nrseqProteinId;
    private double coverage;
    private String userAnnotation;
    private ProteinUserValidation userValidation;
    private List<T> peptides;

    
    public BaseProteinferProtein() {
        peptides = new ArrayList<T>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProteinferId() {
        return pinferId;
    }

    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public int getNrseqProteinId() {
        return nrseqProteinId;
    }

    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }

    public double getCoverage() {
        return Math.round(coverage*100.0) / 100.0;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public String getUserAnnotation() {
        return userAnnotation;
    }

    public void setUserAnnotation(String userAnnotation) {
        this.userAnnotation = userAnnotation;
    }

    public ProteinUserValidation getUserValidation() {
        return userValidation;
    }

    public void setUserValidation(ProteinUserValidation userValidation) {
        this.userValidation = userValidation;
    }

    public List<T> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<T> peptides) {
        this.peptides = peptides;
    }

    public int getPeptideCount() {
        return peptides.size();
    }

    public int getUniquePeptideCount() {
        int cnt = 0;
        for(BaseProteinferPeptide<S> peptide: peptides)
            if(peptide.isUniqueToProtein())
                cnt++;
        return cnt;
    }

    public int getSpectralCount() {
        int cnt = 0;
        for(BaseProteinferPeptide<S> peptide: peptides)
            cnt += peptide.getSpectralCount();
        return cnt;
    }

}