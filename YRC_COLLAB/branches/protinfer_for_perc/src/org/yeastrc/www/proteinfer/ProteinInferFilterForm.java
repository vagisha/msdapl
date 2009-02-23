package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ProteinInferFilterForm extends ActionForm {

    private int pinferId;
    
    private double minCoverage = 0.0;
    private int minPeptides = 1;
    private int minUniquePeptides = 0;
    private int minSpectrumMatches = 1;
    
    private boolean joinGroupProteins = true;
    private boolean showAllProteins = true;
    
    private boolean exhaustiveCommonNameLookup = false;
    
    private String accessionLike = null;
    private String descriptionLike = null;
    private String[] validationStatus = new String[]{"All"};
    
    public ProteinInferFilterForm () {}
    
    public void reset() {
        minCoverage = 0.0;
        minPeptides = 1;
        minUniquePeptides = 0;
        minSpectrumMatches = 1;
        joinGroupProteins = true;
        showAllProteins = true;
        accessionLike = null;
    }
    
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        return errors;
    }

    public int getPinferId() {
        return pinferId;
    }

    public void setPinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public double getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(double inCoverage) {
        this.minCoverage = inCoverage;
    }

    public int getMinPeptides() {
        return minPeptides;
    }

    public void setMinPeptides(int minPeptides) {
        this.minPeptides = minPeptides;
    }

    public int getMinUniquePeptides() {
        return minUniquePeptides;
    }

    public void setMinUniquePeptides(int minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }

    public int getMinSpectrumMatches() {
        return minSpectrumMatches;
    }

    public void setMinSpectrumMatches(int minSpectrumMatches) {
        this.minSpectrumMatches = minSpectrumMatches;
    }

    public boolean isJoinGroupProteins() {
        return joinGroupProteins;
    }

    public void setJoinGroupProteins(boolean joinGroupProteins) {
        this.joinGroupProteins = joinGroupProteins;
    }

    public boolean isShowAllProteins() {
        return showAllProteins;
    }

    public void setShowAllProteins(boolean showAllProteins) {
        this.showAllProteins = showAllProteins;
    }
    
    public String getAccessionLike() {
        if(accessionLike == null || accessionLike.trim().length() == 0)
            return null;
        else
            return accessionLike.trim();
            
    }
    
    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    public String getDescriptionLike() {
        if(descriptionLike == null || descriptionLike.trim().length() == 0)
            return null;
        else
            return descriptionLike.trim();
            
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }

    public String[] getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String[] validationStatus) {
        for(String vs: validationStatus)
            System.out.println(vs);
        this.validationStatus = validationStatus;
    }

    public boolean isExhaustiveCommonNameLookup() {
        return exhaustiveCommonNameLookup;
    }

    public void setExhaustiveCommonNameLookup(boolean exhaustiveCommonNameLookup) {
        this.exhaustiveCommonNameLookup = exhaustiveCommonNameLookup;
    }
   
}
