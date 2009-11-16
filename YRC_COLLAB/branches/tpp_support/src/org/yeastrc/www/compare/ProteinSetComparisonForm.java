/**
 * ProteinferRunComparisionForm.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GOUtils;

/**
 * 
 */
public class ProteinSetComparisonForm extends DatasetFiltersForm {

    private int pageNum = 1;
    
    private boolean download = false;
    private boolean goEnrichment = false;
    private boolean goEnrichmentGraph = false;
    
    
    private int goAspect = GOUtils.BIOLOGICAL_PROCESS;
    private int speciesId;
    private String goEnrichmentPVal = "0.01";
    
    
    public void reset() {
        super.reset();
    }
    
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        // we need atleast two datasets runs to compare
        if (getSelectedRunCount() < 2) {
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more experiments to compare."));
        }
        return errors;
    }

    //-----------------------------------------------------------------------------
    // Download
    //-----------------------------------------------------------------------------
    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    //-----------------------------------------------------------------------------
    // GO Enrichment
    //-----------------------------------------------------------------------------
    public int getGoAspect() {
        return goAspect;
    }

    public void setGoAspect(int goAspect) {
        this.goAspect = goAspect;
    }

    public String getGoEnrichmentPVal() {
        return goEnrichmentPVal;
    }

    public void setGoEnrichmentPVal(String goEnrichmentPVal) {
        this.goEnrichmentPVal = goEnrichmentPVal;
    }

    public boolean isGoEnrichment() {
        return goEnrichment;
    }

    public void setGoEnrichment(boolean goEnrichment) {
        this.goEnrichment = goEnrichment;
    }
    
    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public boolean isGoEnrichmentGraph() {
        return goEnrichmentGraph;
    }

    public void setGoEnrichmentGraph(boolean goEnrichmentGraph) {
        this.goEnrichmentGraph = goEnrichmentGraph;
    }
}
