package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultFilterCriteria;

public class PeptideProphetFilterResultsForm extends AnalysisFilterResultsForm {

    private String minProbability = null;
    private String maxProbability = null;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        if(minProbability != null && minProbability.trim().length() > 0) {
            try{Double.parseDouble(minProbability);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. probability"));}
        }
        if(maxProbability != null && maxProbability.trim().length() > 0) {
            try{Double.parseDouble(maxProbability);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. probability"));}
        }
        
        return errors;
    }
    
    
    public String getMinProbability() {
        return this.minProbability;
    }
    public Double getMinProbabilityDouble() {
        if(minProbability != null && minProbability.trim().length() > 0)
            return Double.parseDouble(minProbability);
        return null;
    }
    public void setMinProbability(String minProbability) {
        this.minProbability = minProbability;
    }
    
    public String getMaxProbability() {
        return maxProbability;
    }
    public Double getMaxProbabilityDouble() {
        if(maxProbability != null && maxProbability.trim().length() > 0)
            return Double.parseDouble(maxProbability);
        return null;
    }
    public void setMaxProbability(String maxProbability) {
        this.maxProbability = maxProbability;
    }
    
    
    public PeptideProphetResultFilterCriteria getFilterCriteria() {
        PeptideProphetResultFilterCriteria criteria = new PeptideProphetResultFilterCriteria();
        
        criteria.setMinScan(getMinScanInt());
        criteria.setMaxScan(getMaxScanInt());
        
        criteria.setMinCharge(getMinChargeInt());
        criteria.setMaxCharge(getMaxChargeInt());
        
        criteria.setMinObservedMass(getMinObsMassDouble());
        criteria.setMaxObservedMass(getMaxObsMassDouble());
        
        criteria.setMinRetentionTime(getMinRTDouble());
        criteria.setMaxRetentionTime(getMaxRTDouble());
        
        criteria.setPeptide(getPeptide());
        criteria.setExactPeptideMatch(getExactPeptideMatch());
        
        criteria.setShowOnlyModified(isShowModified() && !isShowUnmodified());
        criteria.setShowOnlyUnmodified(isShowUnmodified() && !isShowModified());
        
        criteria.setMinProbability(getMinProbabilityDouble());
        criteria.setMaxProbability(getMaxProbabilityDouble());
        
        criteria.setFileNames(filteredFileNames());
        
        return criteria;
    }
   
}
