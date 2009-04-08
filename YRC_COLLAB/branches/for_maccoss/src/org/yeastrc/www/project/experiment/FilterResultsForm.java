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

    private String experimentId;
    private String program;
    private String filename;
    private int numResults;
    private int numResultsFiltered;
    
    private Integer minScan;
    private Integer maxScan;
    
    private Integer minCharge;
    private Integer maxCharge;
    
    private Double minRT;
    private Double maxRT;
    
    private Double minObsMass;
    private Double maxObsMass;
    
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
    
    public Integer getMinScan() {
        return minScan;
    }
    public void setMinScan(Integer minScan) {
        if(minScan != null && minScan == 0)
            this.minScan = null;
        else
            this.minScan = minScan;
    }
    
    public Integer getMaxScan() {
        return maxScan;
    }
    public void setMaxScan(Integer maxScan) {
        if(maxScan != null && maxScan == 0)
            this.maxScan = null;
        else
            this.maxScan = maxScan;
    }
    
    public Integer getMinCharge() {
        return minCharge;
    }
    public void setMinCharge(Integer minCharge) {
        if(minCharge != null && minCharge == 0)
            this.minCharge = null;
        else
            this.minCharge = minCharge;
    }
    
    public Integer getMaxCharge() {
        return maxCharge;
    }
    public void setMaxCharge(Integer maxCharge) {
        if(maxCharge != null && maxCharge == 0)
            this.maxCharge = null;
        else
            this.maxCharge = maxCharge;
    }
    
    public Double getMinRT() {
        return minRT;
    }
    public void setMinRT(Double minRT) {
        if(minRT != null && minRT == 0)
            this.minRT = null;
        else
            this.minRT = minRT;
    }
    
    public Double getMaxRT() {
        return maxRT;
    }
    public void setMaxRT(Double maxRT) {
        if(maxRT != null && maxRT == 0)
            this.maxRT = null;
        else
            this.maxRT = maxRT;
    }
    
    public Double getMinObsMass() {
        return minObsMass;
    }
    public void setMinObsMass(Double minObsMass) {
        if(minObsMass != null && minObsMass == 0)
            this.minObsMass = null;
        else
            this.minObsMass = minObsMass;
    }
    
    public Double getMaxObsMass() {
        return maxObsMass;
    }
    public void setMaxObsMass(Double maxObsMass) {
        if(maxObsMass != null && maxObsMass == 0)
            this.maxObsMass = null;
        else
            this.maxObsMass = maxObsMass;
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

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public int getNumResultsFiltered() {
        return numResultsFiltered;
    }

    public void setNumResultsFiltered(int numResultsFiltered) {
        this.numResultsFiltered = numResultsFiltered;
    }
}
