package edu.uwpr.protinfer.database.dto;

import java.util.List;

public class ProteinferProtein {

    private int id;
    private int pinferId;
    private int clusterId;
    private int groupId;
    private int nrseqProteinId;
    private String accession;
    private double coverage;
    private boolean isParsimonious;
    
    private String userAnnotation;
    private ProteinUserValidation userValidation;
    
    private List<ProteinferPeptide> peptides;
    
    
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
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
    
    public double getCoverage() {
        return coverage;
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
    
    public String getAccession() {
        return accession;
    }
    public void setAccession(String accession) {
        this.accession = accession;
    }
    
    public boolean getIsParsimonious() {
        return this.isParsimonious;
    }
    public void setIsParsimonious(boolean isParsimonious) {
        this.isParsimonious = isParsimonious;
    }
}
