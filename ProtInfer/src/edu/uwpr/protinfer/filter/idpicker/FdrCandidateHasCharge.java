package edu.uwpr.protinfer.filter.idpicker;

import edu.uwpr.protinfer.filter.fdr.FdrCandidate;

public interface FdrCandidateHasCharge extends FdrCandidate {

    public abstract int getCharge();
}
