/**
 * PercolatorFilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;

/**
 * 
 */
public class PercolatorFilterResultsForm extends FilterResultsForm {

//    private int runSearchAnalysisId;
    private int searchAnalysisId;
    
    private Double minQValue = null;
    private Double maxQValue = null;
    
    private Double minPep = null;
    private Double maxPep = null;
    
    private Double minDs = null;
    private Double maxDs = null;
    
    private boolean usePEP = true;
    
    

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return super.validate(mapping, request);
    }
    
    public boolean isUsePEP() {
        return usePEP;
    }

    public void setUsePEP(boolean usePEP) {
        this.usePEP = usePEP;
    }
    
    public Double getMinQValue() {
        return minQValue;
    }
    public void setMinQValue(Double minQValue) {
        if(minQValue != null && minQValue == 0)
            this.minQValue = null;
        else
            this.minQValue = minQValue;
    }
    
    public Double getMaxQValue() {
        return maxQValue;
    }
    public void setMaxQValue(Double maxQValue) {
        if(maxQValue != null && maxQValue == 0)
            this.maxQValue = null;
        else
            this.maxQValue = maxQValue;
    }
    
    public Double getMinPep() {
        return minPep;
    }
    public void setMinPep(Double minPep) {
        if(minPep != null && minPep == 0)
            this.minPep = null;
        else
            this.minPep = minPep;
    }
    
    public Double getMaxPep() {
        return maxPep;
    }
    public void setMaxPep(Double maxPep) {
        if(maxPep != null && maxPep == 0)
            this.maxPep = null;
        else
            this.maxPep = maxPep;
    }
    
    public Double getMinDs() {
        return minDs;
    }

    public void setMinDs(Double minDs) {
        this.minDs = minDs;
    }

    public Double getMaxDs() {
        return maxDs;
    }

    public void setMaxDs(Double maxDs) {
        this.maxDs = maxDs;
    }
    
    public PercolatorResultFilterCriteria getFilterCriteria() {
        PercolatorResultFilterCriteria criteria = new PercolatorResultFilterCriteria();
        
        criteria.setMinScan(getMinScan());
        criteria.setMaxScan(getMaxScan());
        
        criteria.setMinCharge(getMinCharge());
        criteria.setMaxCharge(getMaxCharge());
        
        criteria.setMinObservedMass(getMinObsMass());
        criteria.setMaxObservedMass(getMaxObsMass());
        
        criteria.setMinRetentionTime(getMinRT());
        criteria.setMaxRetentionTime(getMaxRT());
        
        criteria.setPeptide(getPeptide());
        criteria.setExactPeptideMatch(getExactPeptideMatch());
        
        criteria.setShowOnlyModified(isShowModified() && !isShowUnmodified());
        criteria.setShowOnlyUnmodified(isShowUnmodified() && !isShowModified());
        
        criteria.setMinQValue(getMinQValue());
        criteria.setMaxQValue(getMaxQValue());
        
        criteria.setMinPep(getMinPep());
        criteria.setMaxPep(getMaxPep());
        
        criteria.setMinDs(getMinDs());
        criteria.setMaxDs(getMaxDs());
        
        return criteria;
    }

    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
    }

//    public int getRunSearchAnalysisId() {
//        return runSearchAnalysisId;
//    }
//
//    public void setRunSearchAnalysisId(int runSearchAnalysisId) {
//        this.runSearchAnalysisId = runSearchAnalysisId;
//    }
}
