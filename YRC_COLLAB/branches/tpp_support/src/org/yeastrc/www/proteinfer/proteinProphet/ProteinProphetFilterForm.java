package org.yeastrc.www.proteinfer.proteinProphet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.proteinfer.ProteinInferFilterForm;

public class ProteinProphetFilterForm extends ProteinInferFilterForm {

    
    private boolean joinProphetGroupProteins = true;
    private boolean excludeSubsumed = false;
    
    private String minGroupProbability = "0.0";
    private String maxGroupProbability = "1.0";
    
    private String minProteinProbability = "0.0";
    private String maxProteinProbability = "1.0";
    
    
	public ProteinProphetFilterForm () {}
    
    public void reset() {
        
        joinProphetGroupProteins = true;
        excludeSubsumed = false;
        
        minGroupProbability = "0.0";
        maxGroupProbability = "1.0";
        minProteinProbability = "0.0";
        maxProteinProbability = "1.0";
        
        super.reset();
    }
    
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        return errors;
    }

	
    // MIN PROTEIN PROPHET GROUP PROBABILITY
    public String getMinGroupProbability() {
        return minGroupProbability;
    }
    public double getMinGroupProbabilityDouble() {
        if(minGroupProbability == null || minGroupProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minGroupProbability);
    }
    public void setMinGroupProbability(String minProbability) {
        this.minGroupProbability = minProbability;
    }
    
    // MAX PROTEIN PROPHET GROUP PROBABILITY
    public String getMaxGroupProbability() {
        return maxGroupProbability;
    }
    public double getMaxGroupProbabilityDouble() {
        if(maxGroupProbability == null || maxGroupProbability.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxGroupProbability);
    }
    public void setMaxGroupProbability(String maxProbability) {
        this.maxGroupProbability = maxProbability;
    }
    
    // MIN PROTEIN PROPHET PROTEIN PROBABILITY
    public String getMinProteinProbability() {
        return minProteinProbability;
    }
    public double getMinProteinProbabilityDouble() {
        if(minProteinProbability == null || minProteinProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minProteinProbability);
    }
    public void setMinProteinProbability(String minProbability) {
        this.minProteinProbability = minProbability;
    }
    
    // MAX PROTEIN PROPHET PROTEIN PROBABILITY
    public String getMaxProteinProbability() {
        return maxProteinProbability;
    }
    public double getMaxProteinProbabilityDouble() {
        if(maxProteinProbability == null || maxProteinProbability.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxProteinProbability);
    }
    public void setMaxProteinProbability(String maxProbability) {
        this.maxProteinProbability = maxProbability;
    }
    
   
    // PROTEIN GROUPS
    public boolean isJoinProphetGroupProteins() {
        return joinProphetGroupProteins;
    }

    public void setJoinProphetGroupProteins(boolean joinGroupProteins) {
        this.joinProphetGroupProteins = joinGroupProteins;
    }
    
    public boolean isExcludeSubsumed() {
        return excludeSubsumed;
    }

    public void setExcludeSubsumed(boolean excludeSubsumed) {
        this.excludeSubsumed = excludeSubsumed;
    }
    
}
