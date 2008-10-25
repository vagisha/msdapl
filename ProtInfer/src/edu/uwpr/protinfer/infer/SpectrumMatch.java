package edu.uwpr.protinfer.infer;

public interface SpectrumMatch {

    public abstract int getScanNumber();
    
    public abstract int getCharge();
    
    public abstract String getPeptideSequence();
}
