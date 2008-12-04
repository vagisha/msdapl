package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dto.BaseProteinferRun;

public class IdPickerRun extends BaseProteinferRun<IdPickerInputSummary> {

    private int numUnfilteredProteins = -1;
    private int numUnfilteredPeptides = -1;
    
    private List<IdPickerFilter> filters;
    
    public IdPickerRun() {
        super();
        filters = new ArrayList<IdPickerFilter>();
    }
    
    public int getNumUnfilteredProteins() {
        return numUnfilteredProteins;
    }
    public void setNumUnfilteredProteins(int numUnfilteredProteins) {
        this.numUnfilteredProteins = numUnfilteredProteins;
    }
    public int getNumUnfilteredPeptides() {
        return numUnfilteredPeptides;
    }
    public void setNumUnfilteredPeptides(int numUnfilteredPeptides) {
        this.numUnfilteredPeptides = numUnfilteredPeptides;
    }
    
    public List<IdPickerFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<IdPickerFilter> filter) {
        this.filters = filter;
    }
    
    public IdPickerInputSummary getInputSummaryForRunSearch(int runSearchId) {
        for(IdPickerInputSummary input: this.getInputSummaryList()) {
            if(input.getRunSearchId() == runSearchId) {
                return input;
            }
        }
        return null;
    }
}
