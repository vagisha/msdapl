/**
 * PercolatorParams.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;

/**
 * 
 */
public class PercolatorParams {
    
    private double qvalue;
    private boolean hasQvalueCutoff = false;
    private double pep;
    private boolean hasPepCutoff = false;
    private double discriminantScore;
    private boolean hasDiscriminantScoreCutoff = false;
    private IDPickerParams idpParams = null;
    
    private boolean usePeptideLevelScores = false;
    
    public PercolatorParams(IDPickerParams params) {
        
        this.idpParams = params;
        List<IdPickerParam> moreFilters = params.getMoreFilters();
        for(IdPickerParam filter: moreFilters) {
            if(filter.getName().equalsIgnoreCase("qval_percolator")) {
                qvalue = Double.parseDouble(filter.getValue());
                hasQvalueCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("pep_percolator")) {
                pep = Double.parseDouble(filter.getValue());
                hasPepCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("discriminantScore_percolator")) {
                discriminantScore = Double.parseDouble(filter.getValue());
                hasDiscriminantScoreCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("usePeptideScores")) {
            	usePeptideLevelScores = Boolean.parseBoolean(filter.getValue());
            }
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

    public boolean hasQvalueCutoff() {
        return hasQvalueCutoff;
    }

    public boolean hasPepCutoff() {
        return hasPepCutoff;
    }

    public boolean hasDiscriminantScoreCutoff() {
        return hasDiscriminantScoreCutoff;
    }

	public boolean isUsePeptideLevelScores() {
		return usePeptideLevelScores;
	}
}
