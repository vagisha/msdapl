package org.yeastrc.www.proteinfer.proteinProphet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ProteinProphetFilterForm extends ActionForm {

    private int pinferId;
    
    private String minCoverage = "0.0";
    private String maxCoverage = "100.0";
    private String minMolecularWt = "0.0";
    private String maxMolecularWt;
    private String minPi;
    private String maxPi;
    private String minPeptides = "1";
    private String maxPeptides;
    private String minUniquePeptides = "0";
    private String maxUniquePeptides;
    private String minSpectrumMatches = "1";
    private String maxSpectrumMatches;
    
    private boolean joinProphetGroupProteins = true;
    private boolean showAllProteins = true;
    
    private String accessionLike = null;
    private String descriptionLike = null;
    private String descriptionNotLike = null;
    private String[] validationStatus = new String[]{"All"};
    
    private boolean excludeIndistinGroups = false;
    
    private String peptide = null;
    private boolean exactMatch = true;
    
    private String minProbability = "0.0";
    private String maxProbability = "1.0";
    
    public ProteinProphetFilterForm () {}
    
    public void reset() {
        
        // These need to be set to false because if a checkbox is not checked the browser does not
        // send its value in the request.
        // http://struts.apache.org/1.1/faqs/newbie.html#checkboxes
        joinProphetGroupProteins = true;
        excludeIndistinGroups = false;
        exactMatch = false;
        showAllProteins = true;
        
        minCoverage = "0.0";
        minMolecularWt = "0.0";
        minPeptides = "1";
        minUniquePeptides = "0";
        minSpectrumMatches = "1";
        accessionLike = null;
        descriptionLike = null;
        descriptionNotLike = null;
        minProbability = "0.0";
        maxProbability = "1.0";
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

    // MIN PROBABILITY
    public String getMinProbability() {
        return minProbability;
    }
    public double getMinProbabilityDouble() {
        if(minProbability == null || minProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minProbability);
    }
    public void setMinProbability(String minProbability) {
        this.minProbability = minProbability;
    }
    
    // MAX PROBABILITY
    public String getMaxProbability() {
        return maxProbability;
    }
    public double getMaxProbabilityDouble() {
        if(maxProbability == null || maxProbability.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxProbability);
    }
    public void setMaxProbability(String maxProbability) {
        this.maxProbability = maxProbability;
    }
    
    // MIN COVERAGE
    public String getMinCoverage() {
        return minCoverage;
    }
    public double getMinCoverageDouble() {
        if(minCoverage == null || minCoverage.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minCoverage);
    }
    public void setMinCoverage(String minCoverage) {
        this.minCoverage = minCoverage;
    }
    
    // MAX COVERAGE
    public String getMaxCoverage() {
        return maxCoverage;
    }
    public double getMaxCoverageDouble() {
        if(maxCoverage == null || maxCoverage.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxCoverage);
    }
    public void setMaxCoverage(String maxCoverage) {
        this.maxCoverage = maxCoverage;
    }
    
    // MIN MOLECULAR WT.
    public String getMinMolecularWt() {
        return minMolecularWt;
    }
    public double getMinMolecularWtDouble() {
        if(minMolecularWt == null || minMolecularWt.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minMolecularWt);
    }
    public void setMinMolecularWt(String minMolecularWt) {
        this.minMolecularWt = minMolecularWt;
    }
    
    // MAX MOLECULAR WT.
    public String getMaxMolecularWt() {
        return maxMolecularWt;
    }
    public double getMaxMolecularWtDouble() {
        if(maxMolecularWt == null || maxMolecularWt.trim().length() == 0)
            return Double.MAX_VALUE;
        else
            return Double.parseDouble(maxMolecularWt);
    }
    public void setMaxMolecularWt(String maxMolecularWt) {
        this.maxMolecularWt = maxMolecularWt;
    }
    
    // MIN PI
    public String getMinPi() {
        return minPi;
    }
    public double getMinPiDouble() {
        if(minPi == null || minPi.trim().length() == 0)
            return 0;
        else
            return Double.parseDouble(minPi);
    }
    public void setMinPi(String minPi) {
        this.minPi = minPi;
    }
    
    // MAX PI
    public String getMaxPi() {
        return maxPi;
    }
    public double getMaxPiDouble() {
        if(maxPi == null || maxPi.trim().length() == 0)
            return Double.MAX_VALUE;
        else
            return Double.parseDouble(maxPi);
    }
    public void setMaxPi(String maxPi) {
        this.maxPi = maxPi;
    }
    
    // MIN PEPTIDES
    public String getMinPeptides() {
        return minPeptides;
    }
    public int getMinPeptidesInteger() {
        if(minPeptides == null || minPeptides.trim().length() == 0)
            return 1;
        return Integer.parseInt(minPeptides);
    }
    public void setMinPeptides(String minPeptides) {
        this.minPeptides = minPeptides;
    }

    // MAX PEPTIDES
    public String getMaxPeptides() {
        return maxPeptides;
    }
    public int getMaxPeptidesInteger() {
        if(maxPeptides == null || maxPeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxPeptides);
    }
    public void setMaxPeptides(String maxPeptides) {
        this.maxPeptides = maxPeptides;
    }
    
    // MIN UNIQUE PEPTIDES
    public String getMinUniquePeptides() {
        return minUniquePeptides;
    }
    public int getMinUniquePeptidesInteger() {
        if(minUniquePeptides == null || minUniquePeptides.trim().length() == 0)
            return 0;
        else
            return Integer.parseInt(minUniquePeptides);
    }
    public void setMinUniquePeptides(String minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }

    // MAX UNIQUE PEPTIDES
    public String getMaxUniquePeptides() {
        return maxUniquePeptides;
    }
    public int getMaxUniquePeptidesInteger() {
        if(maxUniquePeptides == null || maxUniquePeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        else
            return Integer.parseInt(maxUniquePeptides);
    }
    public void setMaxUniquePeptides(String maxUniquePeptides) {
        this.maxUniquePeptides = maxUniquePeptides;
    }

    // MIN SPECTRUM MATCHES
    public String getMinSpectrumMatches() {
        return minSpectrumMatches;
    }
    public int getMinSpectrumMatchesInteger() {
        if(minSpectrumMatches == null || minSpectrumMatches.trim().length() == 0)
            return 1;
        else
            return Integer.parseInt(minSpectrumMatches);
    }
    public void setMinSpectrumMatches(String minSpectrumMatches) {
        this.minSpectrumMatches = minSpectrumMatches;
    }
    
    // MAX SPECTRUM MATCHES
    public String getMaxSpectrumMatches() {
        return maxSpectrumMatches;
    }
    public int getMaxSpectrumMatchesInteger() {
        if(maxSpectrumMatches == null || maxSpectrumMatches.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxSpectrumMatches);
    }
    public void setMaxSpectrumMatches(String maxSpectrumMatches) {
        this.maxSpectrumMatches = maxSpectrumMatches;
    }

    // PROTEIN GROUPS
    public boolean isJoinProphetGroupProteins() {
        return joinProphetGroupProteins;
    }

    public void setJoinProphetGroupProteins(boolean joinGroupProteins) {
        this.joinProphetGroupProteins = joinGroupProteins;
    }
    
    public boolean isExcludeIndistinProteinGroups() {
        return this.excludeIndistinGroups;
    }
    
    public void setExcludeIndistinProteinGroups(boolean exclude) {
        this.excludeIndistinGroups = exclude;
    }

    public boolean isShowAllProteins() {
        return showAllProteins;
    }

    public void setShowAllProteins(boolean showAllProteins) {
        this.showAllProteins = showAllProteins;
    }
    
    // ACCESSION
    public String getAccessionLike() {
        if(accessionLike == null || accessionLike.trim().length() == 0)
            return null;
        else
            return accessionLike.trim();
            
    }
    
    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    // DESCRIPTION
    public String getDescriptionLike() {
        if(descriptionLike == null || descriptionLike.trim().length() == 0)
            return null;
        else
            return descriptionLike.trim();
            
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }

    public String getDescriptionNotLike() {
        if(descriptionNotLike == null || descriptionNotLike.trim().length() == 0)
            return null;
        else
            return descriptionNotLike.trim();
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }

    // PEPTIDE 
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        if(peptide != null && peptide.trim().length() == 0)
            this.peptide = null;
        else
            this.peptide = peptide;
    }
    
    public boolean getExactPeptideMatch() {
        return exactMatch;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactMatch = exact;
    }
    
    // VALIDATION STATUS
    public String[] getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String[] validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public void setValidationStatusString(String validationStatus) {
        if(validationStatus == null)
            this.validationStatus = new String[0];
        validationStatus = validationStatus.trim();
        String tokens[] = validationStatus.split(",");
        this.validationStatus = new String[tokens.length];
        int idx = 0;
        for(String tok: tokens) {
            this.validationStatus[idx++] = tok.trim();
        }
    }
    
    public String getValidationStatusString() {
        if(this.validationStatus == null)
            return null;
        StringBuilder buf = new StringBuilder();
        for(String status: validationStatus) {
            buf.append(",");
            buf.append(status);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
}
