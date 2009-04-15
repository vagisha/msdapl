/**
 * ComparePeptidesForm.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * 
 */
public class ComparePeptidesForm extends ActionForm {

    private List<Dataset> datasetList = new ArrayList<Dataset>();
    private int nrseqProteinId;
    

    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        return errors;
    }

    public List<Dataset> getDatasetList() {
        return datasetList;
    }

    public void setDatasetList(List<Dataset> datasetList) {
        this.datasetList = datasetList;
    }
    
    public Dataset getDataset(int index) {
        while(index >= datasetList.size()) {
            datasetList.add(new Dataset());
        }
        return datasetList.get(index);
    }
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }

    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
}
