package edu.uwpr.protinfer.infer;

public interface SpectrumMatch {

    public abstract int getHitId();
    
    public abstract int getScanId();
    
    public abstract int getScanNumber();
    
    public abstract int getCharge();
    
    /**
     * Returns the sequence of the peptide for the spectrum match.  Sequence includes any modifications.
     * @return
     */
    public abstract String getPeptideSequence();
    
}
