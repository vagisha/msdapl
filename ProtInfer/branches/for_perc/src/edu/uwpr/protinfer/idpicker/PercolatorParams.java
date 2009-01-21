/**
 * PercolatorParams.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam;

/**
 * 
 */
public class PercolatorParams {
    
    private double qvalue;
    private double pep = -1.0;
    private double discriminantScore = -1.0;
    private IDPickerParams idpParams = null;
    
    public PercolatorParams(IDPickerParams params) {
        
        this.idpParams = params;
        List<IdPickerParam> moreFilters = params.getMoreFilters();
        for(IdPickerParam filter: moreFilters) {
            if(filter.getName().equalsIgnoreCase("qval_percolator"))
                qvalue = Double.parseDouble(filter.getValue());
            else if(filter.getName().equalsIgnoreCase("pep_percolator"))
                pep = Double.parseDouble(filter.getValue());
            else if(filter.getName().equalsIgnoreCase("discriminantScore_percolator"))
                discriminantScore = Double.parseDouble(filter.getValue());
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
    
    public IDPickerParams getIdPickerParams() {
        return idpParams;
    }
}
