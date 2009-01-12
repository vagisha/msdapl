/**
 * ProteinFilterCriteria.java
 * @author Vagisha Sharma
 * Jan 6, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dto;

import edu.uwpr.protinfer.PeptideDefinition;

/**
 * 
 */
public class ProteinFilterCriteria {

    public static enum SORT_BY {
        NUM_PEPT, 
        NUM_UNIQ_PEPT, 
        ACCESSION, 
        COVERAGE, 
        NUM_SPECTRA, 
        GROUP_ID,
        CLUSTER_ID,
        NONE;
        
        public static SORT_BY getSortByForString(String sortBy) {
            if(sortBy == null)
                return NONE;
            else if (sortBy.equalsIgnoreCase(NUM_PEPT.name())) return NUM_PEPT;
            else if (sortBy.equalsIgnoreCase(NUM_UNIQ_PEPT.name())) return NUM_UNIQ_PEPT;
            else if (sortBy.equalsIgnoreCase(ACCESSION.name())) return ACCESSION;
            else if (sortBy.equalsIgnoreCase(COVERAGE.name())) return COVERAGE;
            else if (sortBy.equalsIgnoreCase(NUM_SPECTRA.name())) return NUM_SPECTRA;
            else if (sortBy.equalsIgnoreCase(GROUP_ID.name())) return GROUP_ID;
            else if (sortBy.equalsIgnoreCase(CLUSTER_ID.name())) return CLUSTER_ID;
            else    return NONE;
            
        }
    }
    
    private int numPeptides;
    private int numUniquePeptides;
    private PeptideDefinition peptideDefinition = new PeptideDefinition();
    
    private boolean isParsimonious = false;
    
    private boolean groupProteins = true;
    
    private int numSpectra;
    private double coverage;
    
    private SORT_BY sortBy = SORT_BY.NONE;
    
    public SORT_BY getSortBy() {
        return sortBy;
    }

    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }

    public int getNumPeptides() {
        return numPeptides;
    }
    
    public void setNumPeptides(int numPeptides) {
        this.numPeptides = numPeptides;
    }
    
    public int getNumUniquePeptides() {
        return numUniquePeptides;
    }
    
    public void setNumUniquePeptides(int numUniquePeptides) {
        this.numUniquePeptides = numUniquePeptides;
    }
    
    public PeptideDefinition getPeptideDefinition() {
        return peptideDefinition;
    }
    
    public void setPeptideDefinition(PeptideDefinition peptideDefinition) {
        this.peptideDefinition = peptideDefinition;
    }
    
    public int getNumSpectra() {
        return numSpectra;
    }
    
    public void setNumSpectra(int numSpectra) {
        this.numSpectra = numSpectra;
    }
    
    public double getCoverage() {
        return coverage;
    }
    
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public boolean isParsimonious() {
        return isParsimonious;
    }

    public void setParsimonious(boolean isParsimonious) {
        this.isParsimonious = isParsimonious;
    }

    public boolean isGroupProteins() {
        return groupProteins;
    }

    public void setGroupProteins(boolean groupProteins) {
        this.groupProteins = groupProteins;
    }
}
