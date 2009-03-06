package org.yeastrc.www.proteinfer.alignment;

import java.util.HashSet;
import java.util.Set;

public class Protein {

    private final int pinferProteinId;
    private int nrseqId;
    private String accession;
    private String description;
    private Set<String> coveredFragments;
    private final String originalSequence;

    public Protein(int pinferProteinid, String sequence) {
        this.pinferProteinId = pinferProteinid;
        this.originalSequence = sequence;
        coveredFragments = new HashSet<String>();
    }

    public int getPinferProteinId() {
        return pinferProteinId;
    }
    
    public int getNrseqId() {
        return nrseqId;
    }

    public void setNrseqId(int nrseqId) {
        this.nrseqId = nrseqId;
    }
    
    public void addCoveredFragment(String fragment) {
        this.coveredFragments.add(fragment);
    }

    public Set<String> getCoveredFragments() {
        return coveredFragments;
    }

    public void setCoveredFragments(Set<String> coveredFragments) {
        this.coveredFragments = coveredFragments;
    }
    
    public String getSequence() {
        return originalSequence;
    }

    public int getLength() {
        return originalSequence != null ? originalSequence.length() : 0; 
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

    public void setDescription(String description) {
        this.description = description;
    }
}