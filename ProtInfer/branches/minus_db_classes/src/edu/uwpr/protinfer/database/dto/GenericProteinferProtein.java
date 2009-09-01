/**
 * GenericProteinInferProtein.java
 * @author Vagisha Sharma
 * Dec 30, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.PeptideDefinition;

/**
 * 
 */
public class GenericProteinferProtein <T extends GenericProteinferPeptide<?,?>>{

    private int id;
    private int pinferId;
    private int nrseqProteinId;
    private double coverage;
    private String userAnnotation;
    private ProteinUserValidation userValidation;
    
    private List<T> peptideList;
    
    private PeptideDefinition peptideDefinition;
    
    public GenericProteinferProtein() {
        peptideList = new ArrayList<T>();
        peptideDefinition = new PeptideDefinition(false, false);
    }

    public void setPeptideDefinition(PeptideDefinition peptideDefinition) {
        this.peptideDefinition = peptideDefinition;
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
    
    public String getUserValidationString() {
        return String.valueOf(userValidation.getStatusChar());
    }

    public void setUserValidation(ProteinUserValidation userValidation) {
        this.userValidation = userValidation;
    }

    public List<T> getPeptides() {
        return peptideList;
    }

    public void setPeptides(List<T> peptideList) {
        this.peptideList = peptideList;
    }
    
    public int getPeptideCount() {
        // peptide is uniquely defined by its sequence
        if(!peptideDefinition.isUseCharge() && !peptideDefinition.isUseMods()) 
            return peptideList.size();
        
        else {
            int cnt = 0;
            for(T peptide: peptideList)
                cnt += peptide.getNumDistinctPeptides(peptideDefinition);
            return cnt;
        }
    }
    
    public int getUniquePeptideCount() {
        
        int uniqCnt = 0;
        for(T peptide: peptideList) {
            if(!peptide.isUniqueToProtein())
                continue;
            uniqCnt += peptide.getNumDistinctPeptides(peptideDefinition);
        }
        return uniqCnt;
    }
    
    public int getSpectrumCount() {
        int cnt = 0;
        for(T peptide: peptideList)
            cnt += peptide.getSpectrumCount();
        return cnt;
    }
}
