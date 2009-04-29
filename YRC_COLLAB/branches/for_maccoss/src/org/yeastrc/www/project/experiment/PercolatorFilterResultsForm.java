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
    public void setMinQValue(String minQValue) {
        if(minQValue != null && minQValue.trim().length() > 0) {
            this.minQValue = Double.parseDouble(minQValue.trim());
        }
    }
    
    public Double getMaxQValue() {
        return maxQValue;
    }
//    public String getMaxQValue() {
//        return String.valueOf(maxQValue);
//    }
    public void setMaxQValue(String maxQValue) {
//        this.maxQValue = maxQValue;
        if(maxQValue != null && maxQValue.trim().length() > 0) {
            this.maxQValue = Double.parseDouble(maxQValue.trim());
        }
    }
    
    public Double getMinPep() {
        return minPep;
    }
    public void setMinPep(String minPep) {
        if(minPep != null && minPep.trim().length() > 0) {
            this.minPep = Double.parseDouble(minPep.trim());
        }
    }
    
    public Double getMaxPep() {
        return maxPep;
    }
    public void setMaxPep(String maxPep) {
        if(maxPep != null && maxPep.trim().length() > 0) {
            this.maxPep = Double.parseDouble(maxPep.trim());
        }
    }
    
    public Double getMinDs() {
        return minDs;
    }

    public void setMinDs(String minDs) {
        if(minDs != null && minDs.trim().length() > 0) {
            this.minDs = Double.parseDouble(minDs.trim());
        }
    }

    public Double getMaxDs() {
        return maxDs;
    }

    public void setMaxDs(String maxDs) {
        if(maxDs != null && maxDs.trim().length() > 0) {
            this.maxDs = Double.parseDouble(maxDs.trim());
        }
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
