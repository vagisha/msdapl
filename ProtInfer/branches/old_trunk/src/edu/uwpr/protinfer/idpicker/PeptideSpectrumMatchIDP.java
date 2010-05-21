package edu.uwpr.protinfer.idpicker;

import edu.uwpr.protinfer.filter.fdr.FdrFilterable;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;

public interface PeptideSpectrumMatchIDP extends PeptideSpectrumMatch<SpectrumMatchIDP>, FdrCandidateHasCharge, FdrFilterable {
    
    public abstract double getAbsoluteScore();
    
    public abstract double getRelativeScore();
}
