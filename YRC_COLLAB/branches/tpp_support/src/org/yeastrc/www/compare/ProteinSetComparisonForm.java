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

//    private List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();
//    private List<ProteinferRunFormBean> proteinProphetRuns = new ArrayList<ProteinferRunFormBean>();
//    private List<DTASelectRunFormBean> dtaRuns = new ArrayList<DTASelectRunFormBean>();
//
//    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
//    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
//    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
//    private List<SelectableDataset> xorList = new ArrayList<SelectableDataset>();
    
    private int pageNum = 1;
    
    private boolean download = false;
    private boolean goEnrichment = false;
    private boolean goEnrichmentGraph = false;
    
//    private String searchString;
//    
//    private boolean onlyParsimonious = false;
//    
//    private boolean groupProteins = false;
    
    private int goAspect = GOUtils.BIOLOGICAL_PROCESS;
    private int speciesId;
    private String goEnrichmentPVal = "0.01";
    
    
    public void reset() {
        super.reset();
    }
    
//    public boolean isGroupProteins() {
//        return groupProteins;
//    }
//
//    public void setGroupProteins(boolean groupProteins) {
//        this.groupProteins = groupProteins;
//    }
//
//    public boolean isOnlyParsimonious() {
//        return onlyParsimonious;
//    }
//
//    public void setOnlyParsimonious(boolean onlyParsimonious) {
//        this.onlyParsimonious = onlyParsimonious;
//    }
//
//    public String getSearchString() {
//        return searchString;
//    }
//
//    public void setSearchString(String searchString) {
//        this.searchString = searchString;
//    }

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

//    private int selectedRunCount() {
//        int i = 0;
//        for (ProteinferRunFormBean piRun: piRuns) {
//            if (piRun != null && piRun.isSelected()) i++;
//        }
//        for(DTASelectRunFormBean dtaRun: dtaRuns) {
//            if(dtaRun != null && dtaRun.isSelected()) i++;
//        }
//        return i;
//    }
    
    //-----------------------------------------------------------------------------
    // Protein inference datasets
    //-----------------------------------------------------------------------------
//    public ProteinferRunFormBean getProteinferRun(int index) {
//        while(index >= piRuns.size())
//            piRuns.add(new ProteinferRunFormBean());
//        return piRuns.get(index);
//    }
//    
//    public void setProteinferRunList(List <ProteinferRunFormBean> piRuns) {
//        this.piRuns = piRuns;
//    }
//    
//    public List <ProteinferRunFormBean> getProteinferRunList() {
//        return piRuns;
//    }
//    
//    public List<Integer> getSelectedProteinferRunIds() {
//        List<Integer> ids = new ArrayList<Integer>();
//        for(ProteinferRunFormBean run: piRuns) {
//            if(run != null && run.isSelected())
//                ids.add(run.getRunId());
//        }
//        return ids;
//    }
//    
    //-----------------------------------------------------------------------------
    // Protein Prophet datasets
    //-----------------------------------------------------------------------------
//    public ProteinferRunFormBean getProteinProphetRun(int index) {
//        while(index >= proteinProphetRuns.size())
//            proteinProphetRuns.add(new ProteinferRunFormBean());
//        return proteinProphetRuns.get(index);
//    }
//    
//    public void setProteinProphetRunList(List <ProteinferRunFormBean> peptideProphetRuns) {
//        this.proteinProphetRuns = peptideProphetRuns;
//    }
//    
//    public List <ProteinferRunFormBean> getProteinProphetRunList() {
//        return proteinProphetRuns;
//    }
//    
//    public List<Integer> getSelectedProteinProphetRunIds() {
//        List<Integer> ids = new ArrayList<Integer>();
//        for(ProteinferRunFormBean run: proteinProphetRuns) {
//            if(run != null && run.isSelected())
//                ids.add(run.getRunId());
//        }
//        return ids;
//    }
//    

    //-----------------------------------------------------------------------------
    // DTASelect datasets
    //-----------------------------------------------------------------------------
//    public DTASelectRunFormBean getDtaRun(int index) {
//        while(index >= dtaRuns.size())
//            dtaRuns.add(new DTASelectRunFormBean());
//        return dtaRuns.get(index);
//    }
//    
//    public void setDtaRunList(List <DTASelectRunFormBean> dtaRuns) {
//        this.dtaRuns = dtaRuns;
//    }
//    
//    public List <DTASelectRunFormBean> getDtaRunList() {
//        return dtaRuns;
//    }
//    
//    public List<Integer> getSelectedDtaRunIds() {
//        List<Integer> ids = new ArrayList<Integer>();
//        for(DTASelectRunFormBean run: dtaRuns) {
//            if (run != null && run.isSelected())
//                ids.add(run.getRunId());
//        }
//        return ids;
//    }
    
    //-----------------------------------------------------------------------------
    // AND list
    //-----------------------------------------------------------------------------
//    public SelectableDataset getAndDataset(int index) {
//        while(index >= andList.size()) {
//            andList.add(new SelectableDataset());
//        }
//        return andList.get(index);
//    }
//    
//    public void setAndList(List<SelectableDataset> andList) {
//        this.andList = andList;
//    }
//    
//    public List<SelectableDataset> getAndList() {
//        return andList;
//    }
    
    //-----------------------------------------------------------------------------
    // OR list
    //-----------------------------------------------------------------------------
//    public SelectableDataset getOrDataset(int index) {
//        while(index >= orList.size()) {
//            orList.add(new SelectableDataset());
//        }
//        return orList.get(index);
//    }
//    
//    public void setOrList(List<SelectableDataset> orList) {
//        this.orList = orList;
//    }
//    
//    public List<SelectableDataset> getOrList() {
//        return orList;
//    }
    
    //-----------------------------------------------------------------------------
    // NOT list
    //-----------------------------------------------------------------------------
//    public SelectableDataset getNotDataset(int index) {
//        while(index >= notList.size()) {
//            notList.add(new SelectableDataset());
//        }
//        return notList.get(index);
//    }
//    
//    public void setNotList(List<SelectableDataset> notList) {
//        this.notList = notList;
//    }
//    
//    public List<SelectableDataset> getNotList() {
//        return notList;
//    }
    
    //-----------------------------------------------------------------------------
    // XOR list
    //-----------------------------------------------------------------------------
//    public SelectableDataset getXorDataset(int index) {
//        while(index >= xorList.size()) {
//            xorList.add(new SelectableDataset());
//        }
//        return xorList.get(index);
//    }
//    
//    public void setXorList(List<SelectableDataset> xorList) {
//        this.xorList = xorList;
//    }
//    
//    public List<SelectableDataset> getXorList() {
//        return xorList;
//    }

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
