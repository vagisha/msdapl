package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferRun;

public class IdPickerRun extends GenericProteinferRun<IdPickerInput> {

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

    public List<IdPickerFilter> getSortedFilters() {
        Collections.sort(filters, new Comparator<IdPickerFilter>(){
            public int compare(IdPickerFilter o1, IdPickerFilter o2) {
                return o1.getFilterName().compareTo(o2.getFilterName());
            }});
        return filters;
    }
    
    public void setFilters(List<IdPickerFilter> filter) {
        this.filters = filter;
    }
    
    public IdPickerInput getInputSummaryForRunSearch(int runSearchId) {
        for(IdPickerInput input: this.getInputList()) {
            if(input.getInputId() == runSearchId) {
                return input;
            }
        }
        return null;
    }
}
