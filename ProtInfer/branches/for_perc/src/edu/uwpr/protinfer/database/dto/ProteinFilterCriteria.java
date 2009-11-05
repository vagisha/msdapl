/**
 * ProteinFilterCriteria.java
 * @author Vagisha Sharma
 * Jan 6, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

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
        MOL_WT,
        PI,
        NUM_SPECTRA, 
        GROUP_ID,
        CLUSTER_ID,
        VALIDATION_STATUS,
        NSAF,
        NONE;
        
        public static SORT_BY getSortByForString(String sortBy) {
            if(sortBy == null)
                return NONE;
            else if (sortBy.equalsIgnoreCase(NUM_PEPT.name())) return NUM_PEPT;
            else if (sortBy.equalsIgnoreCase(NUM_UNIQ_PEPT.name())) return NUM_UNIQ_PEPT;
            else if (sortBy.equalsIgnoreCase(ACCESSION.name())) return ACCESSION;
            else if (sortBy.equalsIgnoreCase(MOL_WT.name())) return MOL_WT;
            else if (sortBy.equalsIgnoreCase(PI.name())) return PI;
            else if (sortBy.equalsIgnoreCase(COVERAGE.name())) return COVERAGE;
            else if (sortBy.equalsIgnoreCase(NUM_SPECTRA.name())) return NUM_SPECTRA;
            else if (sortBy.equalsIgnoreCase(GROUP_ID.name())) return GROUP_ID;
            else if (sortBy.equalsIgnoreCase(CLUSTER_ID.name())) return CLUSTER_ID;
            else if (sortBy.equalsIgnoreCase(VALIDATION_STATUS.name())) return VALIDATION_STATUS;
            else if (sortBy.equalsIgnoreCase(NSAF.name())) return NSAF;
            else    return NONE;
            
        }
        
        public static SORT_BY defaultSortBy() {
            return COVERAGE;
        }
    }
        
      public static enum SORT_ORDER {ASC, DESC;
          public static SORT_ORDER getSortByForString(String sortOrder) {
              if(sortOrder == null || sortOrder.equalsIgnoreCase("ASC")) return ASC;
              else return DESC;
          }
          public static SORT_ORDER defaultSortOrder() {
              return DESC;
          }
      }
    
    private int numPeptides;
    private int numMaxPeptides;
    private int numUniquePeptides;
    private int numMaxUniquePeptides;
    private PeptideDefinition peptideDefinition = new PeptideDefinition();
    
    private boolean showParsimonious = false;
    
    private boolean groupProteins = true;
    
    private boolean excludeIndistinGroups = false;
    
    private List<ProteinUserValidation> validationStatus = new ArrayList<ProteinUserValidation>();
    
    private int numSpectra;
    private int numMaxSpectra;
    private double coverage;
    private double maxCoverage;
    
    private String accessionLike;
    private String descriptionLike;
    private String descriptionNotLike;
    
    private String peptide;
    private boolean exactMatch = true;
    
    private double minMolWt;
    private double maxMolWt;
    
    private double minPi;
    private double maxPi;
    
    private SORT_BY sortBy = SORT_BY.NONE;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    
    
    public SORT_ORDER getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }

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

    public boolean showParsimonious() {
        return showParsimonious;
    }

    public void setShowParsimonious(boolean isParsimonious) {
        this.showParsimonious = isParsimonious;
    }

    public boolean isGroupProteins() {
        return groupProteins;
    }

    public void setGroupProteins(boolean groupProteins) {
        this.groupProteins = groupProteins;
    }

    public boolean isExcludeIndistinGroups() {
        return this.excludeIndistinGroups;
    }
    
    public void setExcludeIndistinGroups(boolean exclude) {
        this.excludeIndistinGroups = exclude;
    }
    
    public String getAccessionLike() {
        return accessionLike;
    }

    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    public String getDescriptionLike() {
        return descriptionLike;
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }
    
    public String getDescriptionNotLike() {
        return descriptionNotLike;
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    public List<ProteinUserValidation> getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(List<ProteinUserValidation> validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public void setValidationStatus(String[] validationStatusArr) {
        for(String vs: validationStatusArr) {
            if(vs != null && vs.length() == 1) {
                ProteinUserValidation s = ProteinUserValidation.getStatusForChar(vs.charAt(0));
                if(s != null)   this.validationStatus.add(s);
            }
        }
    }

    public int getNumMaxPeptides() {
        return numMaxPeptides;
    }

    public void setNumMaxPeptides(int numMaxPeptides) {
        this.numMaxPeptides = numMaxPeptides;
    }

    public int getNumMaxUniquePeptides() {
        return numMaxUniquePeptides;
    }

    public void setNumMaxUniquePeptides(int numMaxUniquePeptides) {
        this.numMaxUniquePeptides = numMaxUniquePeptides;
    }

    public int getNumMaxSpectra() {
        return numMaxSpectra;
    }

    public void setNumMaxSpectra(int numMaxSpectra) {
        this.numMaxSpectra = numMaxSpectra;
    }

    public double getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(double maxCoverage) {
        this.maxCoverage = maxCoverage;
    }
    
    
    public void setMinMolecularWt(double molWt) {
        this.minMolWt = molWt;
    }
    
    public double getMinMolecularWt() {
        return minMolWt;
    }
    
    public void setMaxMolecularWt(double molWt) {
        this.maxMolWt = molWt;
    }
    
    public double getMaxMolecularWt() {
        return maxMolWt;
    }
    
    public boolean hasMolecularWtFilter() {
        return (minMolWt != 0 || maxMolWt != Double.MAX_VALUE);
    }
    
    public void setMinPi(double pi) {
        this.minPi = pi;
    }
    
    public double getMinPi() {
        return minPi;
    }
    
    public void setMaxPi(double pi) {
        this.maxPi = pi;
    }
    
    public double getMaxPi() {
        return maxPi;
    }
    
    public boolean hasPiFilter() {
        return (minPi != 0 || maxPi != Double.MAX_VALUE);
    }
    
    
    //-------------------------------------------------------------
    // PEPTIDE FILTER
    //-------------------------------------------------------------
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactMatch = exact;
    }
    public boolean getExactPeptideMatch() {
        return this.exactMatch;
    }
    
    
    public boolean equals(ProteinFilterCriteria o) {
        if(this == o)
            return true;
        if(!(o instanceof ProteinFilterCriteria))
            return false;
        ProteinFilterCriteria that = (ProteinFilterCriteria)o;
        if(this.numPeptides != that.numPeptides)                return false;
        if(this.numMaxPeptides != that.numMaxPeptides)          return false;
        if(this.numUniquePeptides != that.numUniquePeptides)    return false;
        if(this.numMaxUniquePeptides != that.numMaxUniquePeptides)  return false;
        if(this.numSpectra != that.numSpectra)                  return false;
        if(this.numMaxSpectra != that.numMaxSpectra)            return false;
        if(this.coverage != that.coverage)                      return false;
        if(this.maxCoverage != that.maxCoverage)                return false;
        if(this.minMolWt != that.minMolWt)                      return false;
        if(this.maxMolWt != that.maxMolWt)                      return false;
        if(this.minPi != that.minPi)                            return false;
        if(this.maxPi != that.maxPi)                            return false;
        if(this.excludeIndistinGroups != that.excludeIndistinGroups)    return false;
//        if(this.groupProteins != that.groupProteins)            return false;
        if(this.showParsimonious != that.showParsimonious)      return false;
        
        if(this.validationStatus.size() != that.validationStatus.size()) return false;
        else {
            for(ProteinUserValidation vs: this.validationStatus)
                if(!that.validationStatus.contains(vs))  return false;
        }
        
        if(!this.peptideDefinition.equals(that.peptideDefinition))  return false;
        
        if(accessionLike == null) {
            if(that.accessionLike != null)  return false;
        }
        else {
            if(!this.accessionLike.equalsIgnoreCase(that.accessionLike))
                return false;
        }
        
        if(descriptionLike == null) {
            if(that.descriptionLike != null)  return false;
        }
        else {
            if(!this.descriptionLike.equalsIgnoreCase(that.descriptionLike))
                return false;
        }
        
        if(descriptionNotLike == null) {
            if(that.descriptionNotLike != null)  return false;
        }
        else {
            if(!this.descriptionNotLike.equalsIgnoreCase(that.descriptionNotLike))
                return false;
        }
        
        if(peptide == null) {
            if(that.peptide != null)  return false;
        }
        else {
            if(!this.peptide.equalsIgnoreCase(that.peptide))
                return false;
            if(this.exactMatch != that.exactMatch)
                return false;
        }
        return true;
    }
}
