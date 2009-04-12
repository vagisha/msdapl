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

    private int test = 1000;
    
    private List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();

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

        // we need atleast two protein inference runs to compare
        if (selectedRunCount() < 2) {
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or 3 experiments to compare."));
        }
        // we cannot compare more than 3 runs
        if (selectedRunCount() > 3) {
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Cannot compare more than 3 experiments."));
        }
        return errors;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }
    
    public ProteinferRunFormBean getProteinferRun(int index) {
        while(index >= piRuns.size())
            piRuns.add(new ProteinferRunFormBean());
        return piRuns.get(index);
    }
    
    public List <ProteinferRunFormBean> getProteinferRunList() {
        List<ProteinferRunFormBean> selectedRuns = new ArrayList<ProteinferRunFormBean>();
        for (ProteinferRunFormBean run: piRuns)
            if (run != null && run.getRunId() > 0)
                selectedRuns.add(run);
        return selectedRuns;
    }
    
    public List<Integer> getSelectedProteinferRunIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(ProteinferRunFormBean run: piRuns) {
            ids.add(run.getRunId());
        }
        return ids;
    }
    
    public void setProteinferRunList(List <ProteinferRunFormBean> piRuns) {
        this.piRuns = piRuns;
    }
    
    private int selectedRunCount() {
        int i = 0;
        for (ProteinferRunFormBean piRun: piRuns) {
            if (piRun != null && piRun.isSelected()) i++;
        }
        return i;
    }
}
