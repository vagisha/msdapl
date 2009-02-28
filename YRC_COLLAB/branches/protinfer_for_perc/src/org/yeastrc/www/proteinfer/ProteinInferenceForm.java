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
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.proteinfer.ProgramParameters.Param;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

import edu.uwpr.protinfer.database.dto.ProteinferInput.InputType;

/**
 * 
 */
public class ProteinInferenceForm extends ActionForm {

    private int projectId;
    private ProgramParameters programParams;
    private ProteinInferInputSummary inputSummary;
    private InputType inputType;
    private String comments;
    
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
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
        
        // Make sure at least one file was selected
        boolean selected = false;
        for(ProteinInferIputFile input: inputSummary.getInputFiles()) {
            if(input.getIsSelected()) {
                selected = true;
                break;
            }
        }
        if(!selected)
            errors.add("proteinfer", new ActionMessage("error.proteinfer.noinput"));
        
        // Validate the parameter values
        if(inputType == InputType.SEARCH) {
            
        }
        else if(inputType == InputType.ANALYSIS) {
            
        }
        return errors;
    }
    
    public InputType getInputType() {
        return this.inputType;
    }
    
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }
    
    public char getInputTypeChar() {
        return inputType.getShortName();
    }
    
    public void setInputTypeChar(char shortName) {
        this.inputType = InputType.getInputTypeForChar(shortName);
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
