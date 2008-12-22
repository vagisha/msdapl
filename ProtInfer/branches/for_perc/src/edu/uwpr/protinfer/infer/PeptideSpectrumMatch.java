package edu.uwpr.protinfer.infer;


public interface PeptideSpectrumMatch <T extends SpectrumMatch>{

    public abstract int getHitId();
    
    public abstract int getScanId();
    
    public abstract int getCharge();
    
    public abstract T getSpectrumMatch();
    
    /**
     * Returns the sequence of the peptide for the spectrum match.
     * @return
     */
    public abstract String getPeptideSequence();
    
    public abstract PeptideHit getPeptideHit();
}
