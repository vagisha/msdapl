package edu.uwpr.protinfer.infer;

public interface SpectrumMatch {

    public abstract int getHitId();
    
    public abstract int getSourceId();
    
    public abstract int getScanId();
    
    public abstract int getCharge();
    
    /**
     * Returns the sequence with modifications
     * @return
     */
    public abstract String getModifiedSequence();
    
    public abstract int getRank();
    
    public abstract void setRank(int rank);
}
