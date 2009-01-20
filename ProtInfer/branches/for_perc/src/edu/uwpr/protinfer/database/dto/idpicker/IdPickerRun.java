package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferRun;

public class IdPickerRun extends GenericProteinferRun<IdPickerInput> {

    
    private List<IdPickerParam> filters;
    
    public IdPickerRun() {
        super();
        filters = new ArrayList<IdPickerParam>();
    }
    
    public List<IdPickerParam> getFilters() {
        return filters;
    }

    public List<IdPickerParam> getSortedFilters() {
        Collections.sort(filters, new Comparator<IdPickerParam>(){
            public int compare(IdPickerParam o1, IdPickerParam o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        return filters;
    }
    
    public void setFilters(List<IdPickerParam> filter) {
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
