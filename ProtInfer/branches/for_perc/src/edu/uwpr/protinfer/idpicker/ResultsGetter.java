/**
 * ResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;

/**
 * 
 */
public interface ResultsGetter {
    
    public abstract List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun run, IDPickerParams params);

    public abstract List<PeptideSpectrumMatchIDP> getResults(int inputId, IDPickerParams params);
}
