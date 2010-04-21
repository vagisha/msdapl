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
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;

/**
 * 
 */
public class ProteinSetComparisonForm extends DatasetFiltersForm {

    private int pageNum = 1;
    
    // DOWNLOAD options
    private boolean download = false;
    private boolean collapseProteinGroups = false; // used only for downloading results
    private boolean includeDescription = false; // used only when downloading results
    
    // GO ENRICHMENT
    private boolean goEnrichment = false;
    private boolean goEnrichmentGraph = false;
    private int goAspect = GOUtils.BIOLOGICAL_PROCESS;
    private int speciesId;
    private String goEnrichmentPVal = "0.01";
    
    // SORTING
    private SORT_BY sortBy = SORT_BY.NUM_PEPT;
    private SORT_ORDER sortOrder = SORT_ORDER.DESC;

    // CLUSTER
	private boolean cluster = false;
	private String clusteringToken = null;
	private boolean newToken = false;
    
    
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
    
    public boolean isCollapseProteinGroups() {
        return this.collapseProteinGroups;
    }
    
    public void setCollapseProteinGroups(boolean collapse) {
        this.collapseProteinGroups = collapse;
    }
    
    public boolean isIncludeDescriptions() {
        return this.includeDescription;
    }
    
    public void setIncludeDescriptions(boolean include) {
        this.includeDescription = include;
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
    
    //-----------------------------------------------------------------------------
    // Cluster
    //-----------------------------------------------------------------------------
    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }
    
    public String getClusteringToken() {
		return clusteringToken;
	}

	public void setClusteringToken(String clusteringToken) {
		this.clusteringToken = clusteringToken;
	}
	
	public boolean isNewToken() {
		return newToken;
	}

	public void setNewToken(boolean newToken) {
		this.newToken = newToken;
	}
    
    //-----------------------------------------------------------------------------
    // Sorting
    //-----------------------------------------------------------------------------
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
        this.sortBy = SORT_BY.getSortByForString(sortBy);
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
}
