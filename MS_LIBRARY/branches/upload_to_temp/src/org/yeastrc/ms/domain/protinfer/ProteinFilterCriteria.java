/**
 * ProteinFilterCriteria.java
 * @author Vagisha Sharma
 * Jan 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;

import java.util.ArrayList;
import java.util.List;

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
        VALIDATION_STATUS,
        NSAF,
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
    
    private List<ProteinUserValidation> validationStatus = new ArrayList<ProteinUserValidation>();
    
    private int numSpectra;
    private int numMaxSpectra;
    private double coverage;
    private double maxCoverage;
    
    private String accessionLike;
    private String descriptionLike;
    
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
        return true;
    }
}
