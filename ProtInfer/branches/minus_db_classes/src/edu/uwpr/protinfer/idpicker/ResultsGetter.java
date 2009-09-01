/**
 * ResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;

/**
 * 
 */
public interface ResultsGetter {
    
    public abstract List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun idpRun, IDPickerParams params);
    
    public abstract List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(List<IdPickerInput> inputList,
                                Program inputGenerator,
                                IDPickerParams params);

    public abstract List<PeptideSpectrumMatchIDP> getResults(IdPickerRun idpRun, IDPickerParams params);
    
    public abstract List<PeptideSpectrumMatchIDP> getResults(List<IdPickerInput> inputList, Program inputGenerator, IDPickerParams params);
}
