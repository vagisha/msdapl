package org.yeastrc.www.proteinfer.maccoss;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

public class PercolatorProteinInferForm extends ActionForm {

    private ProteinInferInputSummary inputSummary;
    private double qvalueThreshold = 0.05;
    private double pepThreshold = 0.05;

    public PercolatorProteinInferForm () {
        inputSummary = new ProteinInferInputSummary();
    }
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        // TODO make sure at least one file was selected
        // TODO validate the parameter values

//      errors.add("fundingTypes", new ActionMessage("error.project.nofoundationname"));

        return errors;
    }

    public void setInputSummary(ProteinInferInputSummary searchFiles) {
        this.inputSummary = searchFiles;
    }

    public ProteinInferInputSummary getInputSummary() {
        return inputSummary;
    }

    public ProteinInferIputFile getRunSearch(int index) {
        return inputSummary.getInputFile(index);
    }

    public double getQvalueThreshold() {
        return qvalueThreshold;
    }
    
    public void setQvalueThreshold(double qval) {
        this.qvalueThreshold = qval;
    }
    
    public double getPepThreshold() {
        return this.pepThreshold;
    }
    
    public void setPepThreshold(double pepThreshold) {
        this.pepThreshold = pepThreshold;
    }
}
