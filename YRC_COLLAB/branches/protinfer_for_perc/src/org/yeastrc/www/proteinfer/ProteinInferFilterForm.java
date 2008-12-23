package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ProteinInferFilterForm extends ActionForm {

    private int pinferId;
    
    private double minCoverage = 0.0;
    private int minPeptides = 1;
    private int minUniquePeptides = 0;
    private int minSpectrumMatches = 1;
    
    private boolean peptideDef_useMods = false;
    private boolean peptideDef_useCharge = false;
    private boolean peptideDef_useSequence = true;
    
    private boolean joinGroupProteins = true;
    
    public ProteinInferFilterForm () {}
    
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

    public int getPinferId() {
        return pinferId;
    }

    public void setPinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public double getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(double inCoverage) {
        this.minCoverage = inCoverage;
    }

    public int getMinPeptides() {
        return minPeptides;
    }

    public void setMinPeptides(int minPeptides) {
        this.minPeptides = minPeptides;
    }

    public int getMinUniquePeptides() {
        return minUniquePeptides;
    }

    public void setMinUniquePeptides(int minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }

    public int getMinSpectrumMatches() {
        return minSpectrumMatches;
    }

    public void setMinSpectrumMatches(int minSpectrumMatches) {
        this.minSpectrumMatches = minSpectrumMatches;
    }

    public boolean isPeptideDef_useMods() {
        return peptideDef_useMods;
    }

    public void setPeptideDef_useMods(boolean peptideDef_useMods) {
        this.peptideDef_useMods = peptideDef_useMods;
    }

    public boolean isPeptideDef_useCharge() {
        return peptideDef_useCharge;
    }

    public void setPeptideDef_useCharge(boolean peptideDef_useCharge) {
        this.peptideDef_useCharge = peptideDef_useCharge;
    }

    public boolean isPeptideDef_useSequence() {
        return peptideDef_useSequence;
    }

    public void setPeptideDef_useSequence(boolean peptideDef_useSequence) {
        this.peptideDef_useSequence = peptideDef_useSequence;
    }

    public boolean isJoinGroupProteins() {
        return joinGroupProteins;
    }

    public void setJoinGroupProteins(boolean joinGroupProteins) {
        this.joinGroupProteins = joinGroupProteins;
    }
   
}
