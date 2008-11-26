package edu.uwpr.protinfer.database.dto;

import java.util.List;

public class ProteinferProtein {

    private int pinferId;
    private int clusterId;
    private int groupId;
    private int nrseqProteinId;
    private double coverage;
    private boolean isParsimonious;
    
    private String userAnnotation;
    private ProteinUserValidation userValidation;
    
    private List<ProteinferPeptide> peptides;
    
    private String accession = "";
    private String description = "";
    
    
    
    public int getProteinferId() {
        return pinferId;
    }
    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
    
    public int getClusterId() {
        return clusterId;
    }
    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
    
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public String getAccession() {
        return accession;
    }
    public void setAccession(String accession) {
        this.accession = accession;
    }
    
    public String getDescription() {
        return description;
    }
    public String getShortDescription() {
        if(description.length() > 40) {
            return description.substring(0, 40)+" ...";
        }
        else
            return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public List<ProteinferPeptide> getPeptides() {
        return peptides;
    }
    public void setPeptides(List<ProteinferPeptide> peptides) {
        this.peptides = peptides;
    }
    
    public boolean getIsParsimonious() {
        return this.isParsimonious;
    }
    public void setIsParsimonious(boolean isParsimonious) {
        this.isParsimonious = isParsimonious;
    }
    
    public int getPeptideCount() {
        return peptides.size();
    }
    
    public int getUniquePeptideCount() {
        int cnt = 0;
        for(ProteinferPeptide peptide: peptides)
            if(peptide.isUniqueToProtein())
                cnt++;
        return cnt;
    }
    
    public int getSpectralCount() {
        int cnt = 0;
        for(ProteinferPeptide peptide: peptides)
            cnt += peptide.getSpectralCount();
        return cnt;
    }
    
    public boolean matchesPeptideGroup(int peptideGrpId) {
        for(ProteinferPeptide pept: peptides) {
            if(pept.getGroupId() == peptideGrpId)
                return true;
        }
        return false;
    }
}
