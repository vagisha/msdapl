/**
 * PercolatorParams.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;

/**
 * 
 */
public class PercolatorParams extends IDPickerParams {
    
    private double qvalue;
    private double pep = -1.0;
    private double discriminantScore = -1.0;
    
    
    public PercolatorParams(IDPickerParams params) {
        
        List<IdPickerFilter> moreFilters = params.getMoreFilters();
        for(IdPickerFilter filter: moreFilters) {
            if(filter.getFilterName().equalsIgnoreCase("qval_percolator"))
                qvalue = Double.parseDouble(filter.getFilterValue());
            else if(filter.getFilterName().equalsIgnoreCase("pep_percolator"))
                pep = Double.parseDouble(filter.getFilterValue());
            else if(filter.getFilterName().equalsIgnoreCase("discriminantScore_percolator"))
                discriminantScore = Double.parseDouble(filter.getFilterValue());
        }
    }
    
    public double getQvalueCutoff() {
        return qvalue;
    }
    
    public double getPEPCutoff() {
        return pep;
    }
    
    public double getDiscriminantScoreCutoff() {
        return discriminantScore;
    }
}
