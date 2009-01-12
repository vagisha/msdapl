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
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;
import org.yeastrc.www.proteinfer.ProgramParameters.Param;

/**
 * 
 */
public class ProteinInferenceForm extends ActionForm {

    private int projectId;
    private ProgramParameters programParams;
    private ProteinInferInputSummary inputSummary;
    
    public ProteinInferenceForm () {
        inputSummary = new ProteinInferInputSummary();
        programParams = new ProgramParameters();
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
    
    public void setInputSummary(ProteinInferInputSummary inputSummary) {
        this.inputSummary = inputSummary;
    }

    public ProteinInferInputSummary getInputSummary() {
        return inputSummary;
    }
    
    public ProteinInferIputFile getInputFile(int index) {
        return inputSummary.getInputFile(index);
    }
    
    public ProgramParameters getProgramParams() {
        return programParams;
    }
    
    public void setProgramParams(ProgramParameters programParams) {
        this.programParams = programParams;
    }
    public Param getParam(int index) {
        return programParams.getParam(index);
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
