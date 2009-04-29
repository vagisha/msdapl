/**
 * FilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;


/**
 * 
 */
public class FilterResultsForm extends ActionForm {

    
    private String minScan;
    private String maxScan;
    
    private String minCharge;
    private String maxCharge;
    
    private String minRT;
    private String maxRT;
    
    private String minObsMass;
    private String maxObsMass;
    
    private String peptide = null;
    private boolean exactMatch = true;
    
    private boolean showModified = true;
    private boolean showUnmodified = true;
    
    private SORT_BY sortBy;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    
    private int pageNum = 1;
    
    
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        try{Integer.parseInt(minScan);}catch(NumberFormatException e){errors.add(messages)
        return errors;
    }
    
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        // These need to be set to false because if a checkbox is not checked the browser does not
        // send its value in the request.
        // http://struts.apache.org/1.1/faqs/newbie.html#checkboxes
        showModified = false;
        showUnmodified = false;
        exactMatch = false;
    }
    
    public SORT_BY getSortBy() {
        return this.sortBy;
    }
    public String getSortByString() {
        if(sortBy == null)  return null;
        return this.sortBy.name();
    }
    
    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }
    public void setSortByString(String sortBy) {
        this.sortBy = SORT_BY.getSortByForName(sortBy);
    }
    
    
    public SORT_ORDER getSortOrder() {
        return this.sortOrder;
    }
    public String getSortOrderString() {
        if(sortOrder == null)   return null;
        return this.sortOrder.name();
    }
    
    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }
    public void setSortOrderString(String sortOrder) {
        this.sortOrder = SORT_ORDER.getSortByForName(sortOrder);
    }
    
    public String getMinScan() {
        return minScan;
    }
    
    public String getMaxScan() {
        return maxScan;
    }
   
    public String getMinCharge() {
        return minCharge;
    }
    
    public String getMaxCharge() {
        return maxCharge;
    }
    
    public String getMinRT() {
        return minRT;
    }
    
    public String getMaxRT() {
        return maxRT;
    }
    
    public String getMinObsMass() {
        return minObsMass;
    }
    
    public String getMaxObsMass() {
        return maxObsMass;
    }
    
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        if(peptide != null && peptide.length() == 0)
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
    
    public boolean isShowModified() {
        return showModified;
    }
    public void setShowModified(boolean showModified) {
        this.showModified = showModified;
    }
    public boolean isShowUnmodified() {
        return showUnmodified;
    }
    public void setShowUnmodified(boolean showUnmodified) {
        this.showUnmodified = showUnmodified;
    }
    
    public ResultSortCriteria getSortCriteria() {
        ResultSortCriteria criteria = new ResultSortCriteria(sortBy, sortOrder);
        return criteria;
    }

    public void setMinScan(String minScan) {
        this.minScan = minScan;
    }

    public void setMaxScan(String maxScan) {
        this.maxScan = maxScan;
    }

    public void setMinCharge(String minCharge) {
        this.minCharge = minCharge;
    }

    public void setMinRT(String minRT) {
        this.minRT = minRT;
    }

    public void setMaxRT(String maxRT) {
        this.maxRT = maxRT;
    }

    public void setMinObsMass(String minObsMass) {
        this.minObsMass = minObsMass;
    }

    public void setMaxObsMass(String maxObsMass) {
        this.maxObsMass = maxObsMass;
    }

}
