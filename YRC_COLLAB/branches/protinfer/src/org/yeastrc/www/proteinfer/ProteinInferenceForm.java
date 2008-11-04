/**
 * ProteinInferenceForm.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.proteinfer.IDPickerParams;
import org.yeastrc.proteinfer.SearchSummary;
import org.yeastrc.proteinfer.SearchSummary.RunSearch;

/**
 * 
 */
public class ProteinInferenceForm extends ActionForm {

    private IDPickerParams params;
    private SearchSummary search;
    
    public ProteinInferenceForm () {
        params = new IDPickerParams();
        search = new SearchSummary();
    }
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
//      errors.add("fundingTypes", new ActionMessage("error.project.nofoundationname"));

        return errors;
    }
    
    public void setIdPickerParams(IDPickerParams params) {
        this.params = params;
    }

    public void setSearchSummary(SearchSummary search) {
        this.search = search;
    }

    public IDPickerParams getIdPickerParams() {
        return params;
    }
    
    public SearchSummary getSearchSummary() {
        return search;
    }
    
    public RunSearch getRunSearch(int index) {
        return search.getRunSearch(index);
    }
}
