/**
 * SequestFilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.search.sequest.SequestResultFilterCriteria;

/**
 * 
 */
public class SequestFilterResultsForm extends FilterResultsForm {

    private int searchId;
    
    private Double minXCorr_1;
    private Double minXCorr_2;
    private Double minXCorr_3;
    private Double minXCorr_H;
    
    private Double minDeltaCN;
    
    private Double minSp;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return super.validate(mapping, request);
    }
    
    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public Double getMinXCorr_1() {
        return minXCorr_1;
    }

    public void setMinXCorr_1(Double minXCorr_1) {
        if(minXCorr_1 != null && minXCorr_1 == 0)
            this.minXCorr_1 = null;
        else
            this.minXCorr_1 = minXCorr_1;
    }

    public Double getMinXCorr_2() {
        return minXCorr_2;
    }

    public void setMinXCorr_2(Double minXCorr_2) {
        if(minXCorr_2 != null && minXCorr_2 == 0)
            this.minXCorr_2 = null;
        else
            this.minXCorr_2 = minXCorr_2;
    }

    public Double getMinXCorr_3() {
        return minXCorr_3;
    }

    public void setMinXCorr_3(Double minXCorr_3) {
        if(minXCorr_3 != null && minXCorr_3 == 0)
            this.minXCorr_3 = null;
        else
            this.minXCorr_3 = minXCorr_3;
    }

    public Double getMinXCorr_H() {
        return minXCorr_H;
    }

    public void setMinXCorr_H(Double minXCorr_H) {
        if(minXCorr_H != null && minXCorr_H == 0)
            this.minXCorr_H = null;
        else
            this.minXCorr_H = minXCorr_H;
    }

    public Double getMinDeltaCN() {
        return minDeltaCN;
    }

    public void setMinDeltaCN(Double minDeltaCN) {
        if(minDeltaCN != null && minDeltaCN == 0)
            this.minDeltaCN = null;
        else
            this.minDeltaCN = minDeltaCN;
    }
    
    public Double getMinSp() {
        return minSp;
    }

    public void setMinSp(Double minSp) {
        if(minSp != null && minSp == 0)
            this.minSp = null;
        else
            this.minSp = minSp;
    }



    public SequestResultFilterCriteria getFilterCriteria() {
        SequestResultFilterCriteria criteria = new SequestResultFilterCriteria();
        
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
        
        criteria.setMinXCorr_1(getMinXCorr_1());
        criteria.setMinXCorr_2(getMinXCorr_2());
        criteria.setMinXCorr_3(getMinXCorr_3());
        criteria.setMinXCorr_H(getMinXCorr_H());
        
        criteria.setMinDeltaCn(getMinDeltaCN());
        criteria.setMinSp(getMinSp());
        
        return criteria;
    }

}
