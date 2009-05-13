/**
 * ProteinferRunComparisionForm.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * 
 */
public class ProteinSetComparisonForm extends ActionForm {

    private List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();
    private List<DTASelectRunFormBean> dtaRuns = new ArrayList<DTASelectRunFormBean>();

    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
    
    private int pageNum = 1;
    
    private boolean download = false;
    
    private String searchString;
    
    private boolean onlyParsimonious = false;
    
    public boolean isOnlyParsimonious() {
        return onlyParsimonious;
    }

    public void setOnlyParsimonious(boolean onlyParsimonious) {
        this.onlyParsimonious = onlyParsimonious;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<ProteinferRunFormBean> getPiRuns() {
        return piRuns;
    }

    public void setPiRuns(List<ProteinferRunFormBean> piRuns) {
        this.piRuns = piRuns;
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
        if (selectedRunCount() < 2) {
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more experiments to compare."));
        }
        return errors;
    }

    private int selectedRunCount() {
        int i = 0;
        for (ProteinferRunFormBean piRun: piRuns) {
            if (piRun != null && piRun.isSelected()) i++;
        }
        for(DTASelectRunFormBean dtaRun: dtaRuns) {
            if(dtaRun != null && dtaRun.isSelected()) i++;
        }
        return i;
    }
    
    //-----------------------------------------------------------------------------
    // Protein inference datasets
    //-----------------------------------------------------------------------------
    public ProteinferRunFormBean getProteinferRun(int index) {
        while(index >= piRuns.size())
            piRuns.add(new ProteinferRunFormBean());
        return piRuns.get(index);
    }
    
    public void setProteinferRunList(List <ProteinferRunFormBean> piRuns) {
        this.piRuns = piRuns;
    }
    
    public List <ProteinferRunFormBean> getProteinferRunList() {
        return piRuns;
    }
    
    public List<Integer> getSelectedProteinferRunIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(ProteinferRunFormBean run: piRuns) {
            if(run != null && run.isSelected())
                ids.add(run.getRunId());
        }
        return ids;
    }
    
    
    //-----------------------------------------------------------------------------
    // DTASelect datasets
    //-----------------------------------------------------------------------------
    public DTASelectRunFormBean getDtaRun(int index) {
        while(index >= dtaRuns.size())
            dtaRuns.add(new DTASelectRunFormBean());
        return dtaRuns.get(index);
    }
    
    public void setDtaRunList(List <DTASelectRunFormBean> dtaRuns) {
        this.dtaRuns = dtaRuns;
    }
    
    public List <DTASelectRunFormBean> getDtaRunList() {
        return dtaRuns;
    }
    
    public List<Integer> getSelectedDtaRunIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(DTASelectRunFormBean run: dtaRuns) {
            if (run != null && run.isSelected())
                ids.add(run.getRunId());
        }
        return ids;
    }
    
    //-----------------------------------------------------------------------------
    // AND list
    //-----------------------------------------------------------------------------
    public SelectableDataset getAndDataset(int index) {
        while(index >= andList.size()) {
            andList.add(new SelectableDataset());
        }
        return andList.get(index);
    }
    
    public void setAndList(List<SelectableDataset> andList) {
        this.andList = andList;
    }
    
    public List<SelectableDataset> getAndList() {
        return andList;
    }
    
    //-----------------------------------------------------------------------------
    // OR list
    //-----------------------------------------------------------------------------
    public SelectableDataset getOrDataset(int index) {
        while(index >= orList.size()) {
            orList.add(new SelectableDataset());
        }
        return orList.get(index);
    }
    
    public void setOrList(List<SelectableDataset> orList) {
        this.orList = orList;
    }
    
    public List<SelectableDataset> getOrList() {
        return orList;
    }
    
    //-----------------------------------------------------------------------------
    // NOT list
    //-----------------------------------------------------------------------------
    public SelectableDataset getNotDataset(int index) {
        while(index >= notList.size()) {
            notList.add(new SelectableDataset());
        }
        return notList.get(index);
    }
    
    public void setNotList(List<SelectableDataset> notList) {
        this.notList = orList;
    }
    
    public List<SelectableDataset> getNotList() {
        return notList;
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
}
